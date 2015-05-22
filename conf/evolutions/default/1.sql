# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table my_group (
  id                        bigint not null,
  name                      varchar(255),
  owner                     varchar(255),
  members                   varchar(255),
  constraint pk_my_group primary key (id))
;

create table person (
  id                        bigint not null,
  email                     varchar(255),
  fullname                  varchar(255),
  webservice_token          varchar(255),
  confirmation_token        varchar(255),
  password_hash             varchar(255),
  date_creation             timestamp,
  validated                 boolean,
  constraint uq_person_email unique (email),
  constraint pk_person primary key (id))
;

create table template (
  id                        bigint not null,
  name                      varchar(255),
  html                      TEXT,
  parameters                varchar(255),
  owner                     varchar(255),
  groups                    varchar(255),
  constraint pk_template primary key (id))
;

create table token (
  token                     varchar(255) not null,
  user_id                   bigint,
  type                      varchar(8),
  date_creation             timestamp,
  email                     varchar(255),
  constraint ck_token_type check (type in ('password','email')),
  constraint pk_token primary key (token))
;

create sequence my_group_seq;

create sequence person_seq;

create sequence template_seq;

create sequence token_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists my_group;

drop table if exists person;

drop table if exists template;

drop table if exists token;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists my_group_seq;

drop sequence if exists person_seq;

drop sequence if exists template_seq;

drop sequence if exists token_seq;

