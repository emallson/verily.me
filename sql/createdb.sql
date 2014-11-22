create table Signatures(
       key_id varchar(40) primary key,
       signature text
);

create table Identities(
       service varchar(30),
       identifier varchar(30),
       key_id varchar(40) references Signatures(key_id),
       primary key (service, identifier, key_id)
);

create index service_identifier ON Identities (service, identifier);
create index Iden_key_id ON Identities (key_id);
