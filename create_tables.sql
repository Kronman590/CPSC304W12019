DROP TABLE vehicleType CASCADE CONSTRAINTS;
DROP TABLE vehicle CASCADE CONSTRAINTS;
DROP TABLE customer CASCADE CONSTRAINTS;
DROP TABLE reservation CASCADE CONSTRAINTS;
DROP TABLE rental CASCADE CONSTRAINTS;
DROP TABLE return CASCADE CONSTRAINTS;

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

create table vehicle
(
    vlicense varchar(10)   not null PRIMARY KEY,
    make     varchar(20)   not null,
    model    varchar(20)   not null,
    year     number(4, 0)  not null,
    color    varchar(10)   not null,
    odometer number(10, 2) not null,
    status   varchar(11)   not null,
    vtname   varchar(20)   not null,
    location varchar(20)   not null,
    city     varchar(20)   not null,
    foreign key (vtname) references vehicleType,
    constraint CHK_status check (status = 'rented' OR status = 'maintenance' OR status = 'available')
);

create table customer
(
    cellphone number(11)  not null,
    name      varchar(50) not null,
    address   varchar(50) not null,
    dlicense  varchar(20) not null PRIMARY KEY
);

create table reservation
(
    res_confNo          char(10)    not null PRIMARY KEY,
    vtname              varchar(20) not null,
    dlicense            varchar(20) not null,
    rental_fromDateTime timestamp   not null,
    rental_toDateTime   timestamp   not null,
    foreign key (vtname) references vehicleType,
    foreign key (dlicense) references customer
);

create table rental
(
    rental_rid          char(6)     not null PRIMARY KEY,
    vlicense            varchar(10) not null,
    dlicense            varchar(20) not null,
    rental_fromDateTime timestamp   not null,
    rental_toDateTime   timestamp   not null,
    rental_odometer     int         not null,
    rental_cardName     varchar(20) not null,
    rental_cardNo       char(16)    not null,
    rental_ExpDate      char(5)     not null,
    res_confNo          char(10),
    foreign key (vlicense) references vehicle,
    foreign key (dlicense) references customer,
    foreign key (res_confNo) references reservation
);

create table return
(
    rental_rid      char(6)        not null PRIMARY KEY,
    return_dateTime timestamp      not null,
    return_odometer int            not null,
    fulltank        char(1)        not null, /*1 = true, 0 = false*/
    value           number(20,10) not null,
    foreign key (rental_rid) references rental,
    constraint CHK_fulltank check (fulltank = 1 OR fulltank = 0)
);
commit;
