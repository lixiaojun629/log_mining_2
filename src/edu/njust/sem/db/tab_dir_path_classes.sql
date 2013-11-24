/*存储各个聚类后各个类别的目录路径编号及其用户编号*/
use log2;
drop table if exists tab_dir_path_classes;
create table tab_dir_path_classes(
	id int auto_increment primary key,
	class_id int,
	user_id int,
	path_id int
)
