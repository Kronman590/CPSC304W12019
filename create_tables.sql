create table reservation (
	res_confNo char(10) not null PRIMARY KEY,
	vehicleType_vtname varchar(9) not null,
	customer_cellphone char(10) not null,
	rental_fromDate date not null,
	rental_fromTime time not null,
	rental_toDate date not null,
	rental_toTime time not null,
	foreign key (vehicleType_vtname) references vehicleType,
	foreign key (customer_cellphone) references customer,
	foreign key (rental_fromDate, rental_fromTime, rental_toDate, rental_toTime) references rental
);

create table rental (
	rental_rid char(6) not null PRIMARY KEY,
	vehicle_vid char(5) not null,
	customer_cellphone char(10) not null,
	rental_fromDate date not null,
	rental_fromTime time not null,
	rental_toDate date not null,
	rental_toTime time not null,
	rental_odometer int not null,
	rental_cardName varchar(20) not null,
	rental_cardNo char(16) not null,
	rental_ExpDate char(4) not null,
	res_confNo char(10),
	foreign key (vehicle_vid) references vehicle,
	foreign key (customer_cellphone) references customer,
	foreign key (res_confNo) references reservation
);

/* TODO, still just example tables
create table branch ( 
	branch_id integer not null PRIMARY KEY,
	branch_name varchar2(20) not null,
	branch_addr varchar2(50),
	branch_city varchar2(20) not null,
	branch_phone integer 
);

create table driver (
	driver_sin integer not null PRIMARY KEY,
	driver_name varchar(20) not null,
	driver_addr varchar(50) not null,
	driver_city varchar(20) not null,
	driver_birthdate date not null,
	driver_phone integer
);

create table license ( 
	license_no NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	driver_sin integer not null,
	license_type char not null,
	license_class integer,
	license_expiry date not null,
	issue_date date not null,
	branch_id integer not null,
	foreign key (driver_sin) references driver,
	foreign key (branch_id) references branch
);

create table exam (
	driver_sin integer not null,
	branch_id integer not null,
	exam_date date not null,
	exam_type char not null,
	exam_score integer,
	PRIMARY KEY (driver_sin, branch_id, exam_date),
	foreign key (driver_sin) references driver,
	foreign key (branch_id) references branch
);
*/
commit;
