create table Proofs(
       id bigserial primary key,
       proof text
);

create table Identities(
       service varchar(30),
       identifier varchar(30),
       key_id varchar(40),
       proof_id bigint references Proofs(id),
       primary key (service, identifier, key_id)
);

create index service_identifier ON Identities (service, identifier);
create index Iden_key_id ON Identities (key_id);
