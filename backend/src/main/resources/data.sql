INSERT INTO company (id, name, street, zipcode, place, homepage, description, other)
VALUES (11111, 'Anders GmbH', 'In der Wo 122', '32421', 'Andersen', '', 'Andere Softwareentwicklung',
        'Java, HTML5, CSS3, Anderes'),
       (13337, 'Ben & Biggs AG', 'An der Wann 39', '13913', 'Biggsendorf', 'www.benbiggs.com', 'Softwareentwicklung',
        'C#, C++, Java'),
       (66666, 'Chlor Claud', 'Unter dem Wer 1', '11111', 'Chlorhausen', 'www.claud.com', 'Vertrieb',
        'Chlor, Cloud, Kotlin');

INSERT INTO contact (id, name, role, mail, telephone, company_id)
VALUES (11111, 'Alfred Anders', 'Geschaeftsfuehrer', 'alfred@anders.de', '134782442', 11111),
       (13337, 'Ben Big', 'Softwarearchitekt', 'ben@benbiggs.com', '428743409', 13337),
       (66666, 'Claudia Chlor', 'Freiberufler', 'chlor@claud.de', '234984809', 66666);

INSERT INTO contact_point (id, title, date, contact_id, comment)
VALUES (11111, 'Kennenlernen', '2019-02-03 00:00:00', 11111,
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.'),
       (13337, 'Besprechung', '2019-02-05 00:00:00', 11111,
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.'),
       (66666, 'Pöbeln', '2019-01-02 00:00:00', 66666,
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.'),
       (99999, 'Frohe Weihnachten', '2019-01-02 00:00:00', 13337,
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.');

INSERT INTO label (id, title)
VALUES (11111, 'Weihnachten 2018'),
       (13337, 'Cloud Lab Bielefeld'),
       (44444, 'Xing'),
       (66666, 'Cloud-Flyer'),
       (99999, 'JUG');

INSERT INTO contact_point_labels (contact_point_id, label_id)
VALUES (11111, 13337),
       (66666, 66666),
       (66666, 11111),
       (13337, 99999),
       (13337, 66666),
       (66666, 99999),
       (99999, 11111);

INSERT INTO contact_point_types (contact_point_id, label_id)
VALUES (11111, 44444),
       (13337, 44444),
       (66666, 99999),
       (99999, 13337);

INSERT INTO todo (id, description, company_id, expiration, reminder, done)
VALUES (11111, 'Alfred Bescheid geben', 11111, '2019-02-05 00:00:0', '2019-02-04 00:00:0', false),
       (13337, 'Mama anrufen', 11111, '2020-02-05 00:00:0', '2020-02-04 00:00:0', false),
       (66666, 'Organisieren', 11111, '2019-02-07 00:00:0', '2010-02-04 00:00:0', false);