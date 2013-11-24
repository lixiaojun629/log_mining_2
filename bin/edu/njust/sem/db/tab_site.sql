create table tab_site (
	url_id int primary key auto_increment, 
	url varchar(2020) not null, 
	catalog varchar(200)
	);
	
insert into  tab_site1 (url) 
SELECT concat('http://www.made-in-china.com',resource) FROM log2.tab_first; 
insert into  tab_site1 (url) 
SELECT referer FROM log2.tab_first where referer like 'http://www.made-in-china.com/_%'; 
insert into log2.tab_site(url) select distinct url from tab_site1;
