use log2;
drop table if exists tab_user;
create table tab_user (
	id int primary key auto_increment,
	pid char(60),
	ip char(15)
)
insert into tab_user(pid,ip_browser_info) SELECT pid,ip_browser_info FROM log2.tab_first where pid <>'' group by pid; 
insert into tab_user(ip_browser_info)SELECT distinct ip_browser_info FROM log2.tab_first where pid ='';