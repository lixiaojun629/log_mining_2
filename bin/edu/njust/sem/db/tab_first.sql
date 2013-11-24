use log2;
drop table if exists tab_first;
create table tab_first if not exists tab_first(
	id int auto_increment primary key,
	ip char(15) not null,
	visit_time datetime not null,
	method char(10) not null,
	resource varchar(2020) not null,
	protocal char(8),
	status char(3),
	referer varchar(2020),
	browser_info varchar(500),
	cookie varchar(100),
	pid varchar(1000),
	domain varchar(50)
)