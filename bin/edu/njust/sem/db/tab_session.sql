use log2;
drop table if exists tab_session;
create table tab_session (
	session_id int auto_increment primary key,
	url_id int not null,
	user_id int not null,
	ip char(15) not null,
	visit_time datetime not null
)