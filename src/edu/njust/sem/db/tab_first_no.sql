use log2;
drop table if exists tab_first_no;
create table tab_first(
	id int auto_increment primary key,
	user_id int,
	ip char(15) not null,
	visit_time datetime not null,
	method char(10) not null,
	resource int ,
	status char(3),
	referer int
)