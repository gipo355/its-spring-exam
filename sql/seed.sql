use master
go

IF EXISTS(SELECT *
          FROM sys.databases
          WHERE name = 'its_java_exam')
    BEGIN
        DROP DATABASE its_java_exam
    END

CREATE DATABASE its_java_exam
go


USE its_java_exam
go

-- create tables

IF OBJECT_ID(N'Articles', N'U') IS NULL
    BEGIN
        CREATE TABLE "Articles"
        (

            Id       uniqueidentifier primary key default newid(),
            Name     VARCHAR(255) not null,
--         Type should be either finishedProduct or semiLavorato, create enum
--             Type     VARCHAR(255) not null,
            Type     VARCHAR(255) not null,
            constraint chk_type check (Type in ('finishedProduct', 'semiLavorato')),
            Quantity INT          not null, -- meters
        )

    END
go

-- seed

INSERT INTO Articles (Name, Type, Quantity)
VALUES ('Auto A', 'finishedProduct', 4),
       ('Auto B', 'finishedProduct', 5),
       ('Wheels', 'semiLavorato', 100),
       ('Axes', 'semiLavorato', 100),
       ('Telaio A', 'semiLavorato', 100),
       ('Telaio B', 'semiLavorato', 100),
       ('Carrozz A', 'semiLavorato', 100),
       ('Carrozz B', 'semiLavorato', 100),
       ('Viti', 'semiLavorato', 100)
go

IF OBJECT_ID(N'Links', N'U') IS NULL
    BEGIN
        CREATE TABLE "Links"
        (

            Id                uniqueidentifier primary key default newid(),
            Article_id_father uniqueidentifier not null references Articles (Id),
            Article_id_child  uniqueidentifier not null references Articles (Id),
            Need_coefficient  INT              not null
        )

    END
go

-- seed
INSERT INTO Links (Article_id_father, Article_id_child, Need_coefficient)
-- get real ids, a father can have multiple children

VALUES ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto A'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Wheels'), 4),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto A'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Axes'), 2),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto A'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Telaio A'), 1),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto A'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Carrozz A'), 1),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto A'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Viti'), 2),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto B'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Wheels'), 4),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto B'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Axes'), 2),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto B'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Telaio B'), 1),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto B'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Carrozz B'), 1),
       ((SELECT Id
         FROM Articles
         WHERE Name = 'Auto B'), (SELECT Id
                                  FROM Articles
                                  WHERE Name = 'Viti'), 2)
go


IF OBJECT_ID(N'Orders', N'U') IS NULL
    BEGIN
        CREATE TABLE "Orders"
        (

            Id                  uniqueidentifier primary key default newid(),
            Article_id          uniqueidentifier not null references Articles (Id),
            Quantity_to_produce INT              not null,
            Produced            BIT              not null
        )

    END
go

IF OBJECT_ID(N'Needs', N'U') IS NULL
    BEGIN
        CREATE TABLE "Needs"
        (

            Id              uniqueidentifier primary key default newid(),
            Order_id        uniqueidentifier not null references Orders (Id),
            Article_id      uniqueidentifier not null references Articles (Id),
            Quantity_needed INT              not null
        )
    END
