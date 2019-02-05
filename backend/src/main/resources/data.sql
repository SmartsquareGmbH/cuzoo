INSERT INTO company (id, name, street, zipcode, place, homepage, description, other) VALUES
(1, 'Anders GmbH', 'In der Wo 122', '32421', 'Andersen', '', 'Andere Softwareentwicklung', 'Java, HTML5, CSS3, Anderes'),
(2, 'Ben & Biggs AG', 'An der Wann 39', '13913', 'Biggsendorf', 'www.benbiggs.com', 'Softwareentwicklung', 'C#, C++, Java'),
(3, 'Chlor Claud', 'Unter dem Wer 1', '11111', 'Chlorhausen', 'www.claud.com', 'Vertrieb', 'Chlor, Cloud, Kotlin');

INSERT INTO contact (id, name, role, mail, telephone, company_id) VALUES
(1, 'Alfred Anders', 'Geschaeftsfuehrer', 'alfred@anders.de', '134782442', 1),
(2, 'Ben Big', 'Softwarearchitekt', 'ben@benbiggs.com', '428743409', 2),
(3, 'Claudia Chlor', 'Freiberufler', 'chlor@claud.de', '234984809', 3);

INSERT INTO contact_point (id, title, type, date, contact_id, comment) VALUES
(1, 'Kennenlernen', 'Telefon', '2019-02-03 00:00:00', 1,
 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.'),
(2, 'Besprechung', 'Social Media', '2019-02-05 00:00:00', 1,
 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.'),
(3, 'Pöbeln', 'E-Mail', '2019-01-02 00:00:00', 3,
 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.'),
(4, 'Frohe Weihnachten', 'Persönlich', '2019-01-02 00:00:00', 2,
 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.');

INSERT INTO label (id, title) VALUES
(1, 'Weihnachten 2018'),
(2, 'Cloud Lab Bielefeld'),
(3, 'Cloud-Flyer'),
(4, 'JUG');

INSERT INTO contact_point_labels (contact_point_id, label_id) VALUES
(1, 2),
(3, 3),
(3, 1),
(2, 4),
(2, 3),
(3, 4),
(4, 1);

INSERT INTO todo (id, description, company_id, expiration, reminder, done) VALUES
(1, 'Alfred Bescheid geben', 1, '2019-02-05 00:00:0', '2019-02-04 00:00:0', false),
(2, 'Mama anrufen', 1, '2020-02-05 00:00:0', '2020-02-04 00:00:0', false),
(3, 'Organisieren', 1, '2019-02-07 00:00:0', '2010-02-04 00:00:0', false);