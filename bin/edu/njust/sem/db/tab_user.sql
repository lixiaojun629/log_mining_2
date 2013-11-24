use log2;
drop table if exists tab_user;
create table tab_user (
	id int primary key auto_increment,
	pid char(60),
	ip char(15)
)
update  log2.tab_first set ip_browser_info = CONCAT_WS('#',ip,browser_info);
insert into tab_user (ip_browser_info)
SELECT distinct ip_browser_info FROM log2.tab_first where pid = '';