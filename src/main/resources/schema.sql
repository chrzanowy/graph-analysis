create table DATASET
(
    ID   int          not null AUTO_INCREMENT,
    NAME varchar(255) not null,
    LAST_UPDATED timestamp not null,
    PRIMARY KEY (ID)
);

create table NODE
(
    ID         int not null AUTO_INCREMENT,
    NODE_ID    int not null,
    DATASET_ID int not null references DATASET (ID),
    PRIMARY KEY (ID)
);

create table EDGE
(
    ID         int not null AUTO_INCREMENT,
    SOURCE_ID  int not null,
    TARGET_ID  int not null,
    DATASET_ID int not null references DATASET (ID),
    PRIMARY KEY (ID)
);

create table ALGORITHM_STATUS
(
    ID      varchar(255)      not null,
    ENABLED bool default true not null,
    PRIMARY KEY (ID)
);

create table ANALYSIS
(
    ID             int          not null AUTO_INCREMENT,
    DATASET_ID     int          not null references DATASET (ID),
    ALGORITHM_NAME varchar(255) not null references ALGORITHM_STATUS (ID),
    EXECUTION_TIME timestamp not null,
    COMPLETION_TIME timestamp not null,
    ANALYSIS_DATA  blob         not null,
    PRIMARY KEY (ID)
);
