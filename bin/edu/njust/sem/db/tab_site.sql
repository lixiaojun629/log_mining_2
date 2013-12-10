create table tab_site (
	url_id int primary key auto_increment, 
	url varchar(2020) not null, 
	catalog varchar(200)
	);
	
insert into tab_site1 (url) SELECT distinct resource FROM log2.tab_first; 
insert into tab_site1(url) SELECT distinct referer FROM log2.tab_first where referer like 'http://www.made-in-china.com/%/mic/%'; 
insert into tab_site1(url) SELECT distinct referer FROM log2.tab_first where referer like 'http://www.made-in-china.com/%-catalog/%';
delete  from tab_site1 where url like '%?%&%';
insert into tab_site (url)SELECT distinct url FROM log2.tab_site1;
/*以下两个页面，连同首页，算作为根目录*/
insert into tab_site(url,catalog,catalog_id)values('http://www.made-in-china.com/prod/catlist/','root',0);
insert into tab_site(url,catalog,catalog_id)values('http://www.made-in-china.com/productdirectory.do?action=list&pdShowType=2','root',0);