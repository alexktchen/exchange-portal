use exchange_portal;

create table exchange_history
(
    id                   bigint unsigned auto_increment comment 'uuid' primary key,
    uuid                 varchar(50)  default 'NULL'                            not null comment 'Unique identifier (e.g. 89d3o179-abcd-465b-o9ee-e2d5f6ofEld46)',
    amount               decimal(22, 8)                                         null comment 'amount',
    iban                 varchar(50)                                            not null comment 'the account IBAN e.g.CH93-0000-0000-0000-0000-0',
    value_date           timestamp    default current_timestamp()               not null comment 'exchange date',
    create_time          timestamp    default current_timestamp()               not null comment 'create date',
    update_time          timestamp(3) default current_timestamp(3)              not null on update current_timestamp(3) comment 'update date',
    currency             varchar(10)  default 'USD'                             not null comment 'currency e.g. TWD, USD, CNY, GBP, CHF',
    description          varchar(500),
    constraint INDEX2
        unique (uuid)
)
    comment 'exchange history';

create index IDX_ga_order_master_cre
    on exchange_history (create_time);

create index user_id_index
    on exchange_history (iban);

create index user_id_currency
    on exchange_history (currency);
