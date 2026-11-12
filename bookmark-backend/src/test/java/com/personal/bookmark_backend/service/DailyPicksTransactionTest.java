package com.personal.bookmark_backend.service;
import com.personal.bookmark_backend.entity.*;
import com.personal.bookmark_backend.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DailyPicksTransactionTest.Config.class)
class DailyPicksTransactionTest {
 @Configuration @EnableTransactionManagement
 @EnableJpaRepositories(basePackages="com.personal.bookmark_backend.repository")
 @Import({DailyPickSelectionService.class,BookmarkService.class,TagService.class})
 static class Config {
  @Bean DataSource dataSource(){return new DriverManagerDataSource("jdbc:h2:mem:daily;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000","sa","");}
  @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds){
   var factory=new LocalContainerEntityManagerFactoryBean();factory.setDataSource(ds);
   factory.setPackagesToScan("com.personal.bookmark_backend.entity");factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
   factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto","create-drop","hibernate.physical_naming_strategy","org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"));return factory;
  }
  @Bean PlatformTransactionManager transactionManager(jakarta.persistence.EntityManagerFactory emf){return new JpaTransactionManager(emf);}
 }
 @Autowired DailyPickSelectionService picks;
 @Autowired BookmarkService service;
 @Autowired UserRepository users;
 @Autowired FolderRepository folders;
 @Autowired BookmarkRepository books;
 @Autowired BookmarkSourceRepository sources;
 @Autowired DailyPickSelectionRepository selections;
 @Autowired PlatformTransactionManager manager;
 Long userId,folderId;
 @BeforeEach void setup(){
  sources.saveAll(List.of(
      new BookmarkSource("OTHER", "Other", 100, ""),
      new BookmarkSource("EXAMPLE", "Example", 10, "example.net,example.com")));
  new TransactionTemplate(manager).executeWithoutResult(status->{
   var user=new User();user.setEmail(UUID.randomUUID()+"@test.example");user.setPasswordHash("test");userId=users.save(user).getId();
   var folder=new Folder();folder.setUserId(userId);folder.setName("test");folderId=folders.save(folder).getId();
  });
  for(int i=0;i<8;i++) create(false);
 }
 Bookmark create(boolean include){return service.createBookmark(userId,folderId,"https://example.org","title",null,null,"OTHER",null,List.of(),include);}

 @Test void onlyOtherIsUsedForTheOtherSource() {
  var page = PageRequest.of(0, 20);
  assertEquals(8, service.searchBookmarks(userId, null, "OTHER", page).getTotalElements());
  assertTrue(service.searchBookmarks(userId, null, "WEB", page).isEmpty());
  assertEquals(Map.of("OTHER", 8L), service.countSearchResultsBySource(userId, null));

  assertThrows(IllegalArgumentException.class, () -> service.createBookmark(
      userId, folderId, "https://example.org", "title", null, null, "WEB", null, List.of(), false));
  var bookmark = create(false);
  assertThrows(IllegalArgumentException.class, () -> service.updateBookmark(
      userId, bookmark.getId(), bookmark.getUrl(), bookmark.getTitle(), null, null, "WEB", null, List.of()));
 }

 @Test void readsTheSourceCatalogAndMatchesConfiguredDomains() {
  assertEquals(List.of("EXAMPLE", "OTHER"),
      service.getBookmarkSources().stream().map(BookmarkSource::getCode).toList());
  var matched = service.createBookmark(userId, folderId, "https://www.example.com/post",
      "Example", null, null, "OTHER", null, List.of(), false);
  var unmatched = service.createBookmark(userId, folderId, "https://example.com.example.org/post",
      "Unmatched", null, null, "OTHER", null, List.of(), false);

  assertEquals("EXAMPLE", matched.getSource());
  assertEquals("OTHER", unmatched.getSource());
  assertEquals(1, service.searchBookmarks(userId, null, "EXAMPLE", PageRequest.of(0, 20)).getTotalElements());
 }
 @Test void uncheckedDoesNotTouchSelections(){picks.select(userId,false,null);var ids=selections.findAllByUserIdOrderByPositionAsc(userId).stream().map(DailyPickSelection::getId).toList();create(false);assertEquals(ids,selections.findAllByUserIdOrderByPositionAsc(userId).stream().map(DailyPickSelection::getId).toList());}
 @Test void concurrentGetsAndRefreshesRemainThreeUniqueRows() throws Exception {
  var executor=Executors.newFixedThreadPool(4);
  try {var tasks=new ArrayList<Callable<Void>>();for(int i=0;i<12;i++){final boolean refresh=i%2==0;tasks.add(()->{picks.select(userId,refresh,null);return null;});}
   for(var result:executor.invokeAll(tasks))result.get(20,TimeUnit.SECONDS);
  } finally {executor.shutdownNow();}
  var rows=selections.findAllByUserIdOrderByPositionAsc(userId);assertEquals(3,rows.size());assertEquals(3,rows.stream().map(DailyPickSelection::getBookmarkId).distinct().count());assertEquals(List.of(1,2,3),rows.stream().map(DailyPickSelection::getPosition).toList());
 }
 @Test void failedOuterTransactionRollsBackBookmarkAndSelection(){
  picks.select(userId,false,null);long count=books.countByUserId(userId);
  var before=selections.findAllByUserIdOrderByPositionAsc(userId).stream().map(DailyPickSelection::getBookmarkId).toList();
  assertThrows(RuntimeException.class,()->new TransactionTemplate(manager).executeWithoutResult(status->{
   create(true);
   var duplicate=new DailyPickSelection();duplicate.setUserId(userId);duplicate.setBookmarkId(before.get(0));
   duplicate.setPosition(1);duplicate.setSelectionDate(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Taipei")));
   selections.saveAndFlush(duplicate);
  }));
  assertEquals(count,books.countByUserId(userId));assertEquals(before,selections.findAllByUserIdOrderByPositionAsc(userId).stream().map(DailyPickSelection::getBookmarkId).toList());
 }
 @Test void deletingSelectedBookmarkRemovesRelationAndFillsGap(){
  var previous=picks.select(userId,false,null).picks();service.deleteBookmark(userId,previous.get(0).getId());
  assertEquals(2,selections.findAllByUserIdOrderByPositionAsc(userId).size());
  var next=picks.select(userId,false,null).picks();assertEquals(3,next.size());assertTrue(next.stream().anyMatch(b->b.getId().equals(previous.get(1).getId())));
 }
}
