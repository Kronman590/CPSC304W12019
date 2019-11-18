create table reservation
(
    res_confNo         char(10)   not null PRIMARY KEY,
    vehicleType_vtname varchar(9) not null,
    customer_cellphone char(10)   not null,
    rental_fromDate    date       not null,
    rental_fromTime    time       not null,
    rental_toDate      date       not null,
    rental_toTime      time       not null,
    foreign key (vehicleType_vtname) references vehicleType,
    foreign key (customer_cellphone) references customer,
    foreign key (rental_fromDate, rental_fromTime, rental_toDate, rental_toTime) references rental
);

create table reserveIncludes
(
    res_confno char(10) not null,
    etname     char(20) not null,
    PRIMARY KEY (res_confno, etname),
    foreign key (res_confno) references reservation,
    foreign key (etname) references equipType
);

create table rental
(
    rental_rid         char(6)     not null PRIMARY KEY,
    vehicle_vid        char(5)     not null,
    customer_cellphone char(10)    not null,
    rental_fromDate    date        not null,
    rental_fromTime    time        not null,
    rental_toDate      date        not null,
    rental_toTime      time        not null,
    rental_odometer    int         not null,
    rental_cardName    varchar(20) not null,
    rental_cardNo      char(16)    not null,
    rental_ExpDate     char(4)     not null,
    res_confNo         char(10),
    foreign key (vehicle_vid) references vehicle,
    foreign key (customer_cellphone) references customer,
    foreign key (res_confNo) references reservation
);

create table rentIncludes
(
    rental_rid char(6) not null,
    eid        integer not null,
    PRIMARY KEY (rental_rid, eid),
    foreign key (rental_rid) references rental,
    foreign key (eid) references equipment
);

create table vehicleType
(
    vtname   varchar(20)   not null PRIMARY KEY,
    features varchar(50)   not null,
    hrate    number(10, 2) not null,
    drate    number(10, 2) not null,
    wrate    number(10, 2) not null,
    hirate   number(10, 2) not null,
    dirate   number(10, 2) not null,
    wirate   number(10, 2) not null,
    krate    number(10, 2) not null
);

create table customer
(
    cellphone number(11)  not null PRIMARY KEY,
    name      varchar(50) not null,
    address   varchar(50) not null,
    dlicense  varchar(20) not null
);

create table clubMember
(
    cellphone number(11)    not null PRIMARY KEY,
    points    number(10, 2) not null,
    fees      number(10, 2) not null,
    foreign key (cellphone) references customer
)

create table vehicle
(
    vid      integer       not null PRIMARY KEY,
    vlicense varchar(10)   not null,
    make     varchar(20)   not null,
    model    varchar(20)   not null,
    year     number(4, 0)  not null,
    color    varchar(10)   not null,
    odometer number(10, 2) not null,
    status   char(8)       not null,
    vtname   varchar(20)   not null,
    location varchar(20)   not null,
    city     varchar(20)   not null,
    foreign key (vtname) references vehicleType,
    foreign key (location, city) references branch,
    constraint CHK_status check (status = 'for_rent' OR status = 'for_sale' )
);

create table branch
(
    location varchar(20),
    city     varchar(20),
    PRIMARY KEY (location, city)
);

create table equipment
(
    eid      integer     not null PRIMARY KEY,
    etname   varchar(20) not null,
    status   varchar(13) not null,
    location varchar(20) not null,
    city     varchar(20) not null,
    foreign key (location, city) references branch,
    foreign key (etname) references equipType,
    constraint CHK_status check (status = 'available' OR status = 'rented' OR status = 'not_available')
);

create table equipType
(
    etname varchar(20)   not null PRIMARY KEY,
    drate  number(10, 2) not null,
    hrate  number(10, 2) not null
);

create table EForV
(
    etname varchar(20) not null,
    vtname varchar(20) not null,
    PRIMARY KEY (etname, vtname),
    foreign key (etname) references equipType,
    foreign key (vtname) references vehicleType
);

create table return
(
    rental_rid  char(6)       not null PRIMARY KEY,
    return_date date          not null,
    return_time time          not null,
    fulltank    char(1)       not null, /*1 = true, 0 = false*/
    value       number(10, 20 not null,
    foreign key (rental_rid) references rental
);

create table timePeriod
(
    fromDate date not null,
    fromTime time not null,
    toDate   date not null,
    toTime   time not null,
    PRIMARY KEY (fromDate, fromTime, toDate, toTime)
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
