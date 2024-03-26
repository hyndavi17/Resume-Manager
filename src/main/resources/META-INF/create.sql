DROP DATABASE ResumeManager;
CREATE DATABASE ResumeManager;
USE ResumeManager;
CREATE TABLE IF NOT EXISTS Account (
    id int unsigned NOT NULL AUTO_INCREMENT,
    username varchar (32) NOT NULL,
    password varchar (255) NOT NULL,
    security_question varchar ( 255) NOT NULL,
    security_answer varchar ( 128) NOT NULL,
    email varchar ( 128) NOT NULL,
    two_fa_status int NOT NULL,
    profile_picture varchar ( 255) DEFAULT NULL,
    cell_phone_number varchar ( 24) DEFAULT NULL,
    cell_phone_carrier varchar ( 32) DEFAULT NULL,
    role varchar ( 32) DEFAULT 'employee',
    suspended_until TIMESTAMP DEFAULT NULL,
    PRIMARY KEY ( id)
);
CREATE TABLE IF NOT EXISTS BasicInfo (
    id int unsigned NOT NULL,
    first_name varchar ( 32) NOT NULL,
    middle_name varchar ( 32) DEFAULT NULL,
    last_name varchar ( 32) NOT NULL,
    dob DATE ,
    summary varchar( 1204) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES Account ( id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Address (
    id int unsigned NOT NULL,
    address1 varchar ( 128) NOT NULL,
    address2 varchar ( 128) DEFAULT NULL,
    city varchar ( 64) NOT NULL,
    state_name varchar ( 32) NOT NULL,
    zipcode varchar ( 10) NOT NULL,
    country varchar ( 255) NOT NULL,
    email varchar ( 128) NOT NULL,
    phone_number varchar ( 24) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES Account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Experience (
    id int unsigned NOT NULL AUTO_INCREMENT,
    user int unsigned NOT NULL,
    organization varchar ( 255) DEFAULT NULL,
    title varchar ( 255) DEFAULT NULL,
    currentlyWorking BOOLEAN NOT NULL DEFAULT 0,
    startDate DATE NOT NULL,
    endDate DATE NULL,
    responsibilities TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user) REFERENCES Account (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS Education (
    id int unsigned NOT NULL AUTO_INCREMENT,
    user int unsigned NOT NULL,
    instituteName varchar ( 255) DEFAULT NULL,
    degree varchar ( 255) NOT NULL,
    startDate date ,
    graduationDate date ,
    description TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user) REFERENCES Account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Skill (
    user int unsigned NOT NULL,
    name varchar (128) NOT NULL,
    PRIMARY KEY (user, name),
    FOREIGN KEY (user) REFERENCES Account ( id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Certificate (
    id int unsigned NOT NULL AUTO_INCREMENT, user int unsigned NOT NULL,
    name varchar ( 255) NOT NULL,
    dateObtained date ,
    PRIMARY KEY (id),
    FOREIGN KEY ( user) REFERENCES Account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Attachment (
    id int unsigned NOT NULL AUTO_INCREMENT,
    user int unsigned NOT NULL,
    name varchar ( 255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY ( user) REFERENCES Account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Project (
    id int unsigned NOT NULL AUTO_INCREMENT,
    manager int unsigned NOT NULL,
    name varchar ( 255) NOT NULL,
    client varchar ( 255) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    proposal_date DATE DEFAULT NULL,
    status int NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    FOREIGN KEY (manager) REFERENCES Account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ProjectEmployee (
    employee int unsigned NOT NULL,
    project int unsigned NOT NULL,
    position varchar ( 255) DEFAULT NULL,
    dateAdded timestamp,
    PRIMARY KEY ( employee, project),
    FOREIGN KEY (employee) REFERENCES Account (id) ON DELETE CASCADE,
    FOREIGN KEY ( project) REFERENCES Project (id) ON DELETE CASCADE
);

CREATE TABLE Template (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    content TEXT NOT NULL
);

CREATE TABLE Configuration(
    id int unsigned NOT NULL AUTO_INCREMENT,
    config_key varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    value varchar(255) NOT NULL,
    PRIMARY KEY ( id)
);

INSERT INTO Account (id, username, password, security_question, security_answer, email, two_fa_status, profile_picture, role)
VALUES (1, 'employee', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your middle name?', 'N/A', 'yoseph@vt.edu', 0, 'defaultUserPhoto.png', 'employee'),
       (2,'hyndavie', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your full name?', 'N/A', 'hyndavi@vt.edu', 0, 'defaultUserPhoto.png', 'employee'),
       (3,'nikhitae', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your full name?', 'N/A', 'nikhita@vt.edu', 0, 'defaultUserPhoto.png', 'employee'),
       (4,'yosepha', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your middle name?', 'N/A', 'yoseph@vt.edu', 0, 'defaultUserPhoto.png', 'admin'),
       (5,'admin', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your full name?', 'N/A', 'ayn@vt.edu', 0, 'defaultUserPhoto.png', 'admin'),
       (6,'nikhita', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your full name?', 'N/A', 'nikhita@vt.edu', 0, 'defaultUserPhoto.png', 'admin'),
       (7,'manager', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your middle name?', 'N/A', 'yoseph@vt.edu', 0, 'defaultUserPhoto.png', 'manager'),
       (8,'hyndavi', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your full name?', 'N/A', 'ayn@vt.edu', 0, 'defaultUserPhoto.png', 'manager'),
       (9,'nikhitam', 'sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH', 'What is your full name?', 'N/A', 'nikhita@vt.edu', 0, 'defaultUserPhoto.png', 'manager'),
       (10,'lynne','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"In what city or town did your mother and father meet?",','I don''t know','lynne.simpson@gmail.com',0,'10.png','employee'),
       (11,'brittany','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"In what city or town were you born?",','I don''t know','brittany.tyler@gmail.com',0,'11.png','employee'),
       (12,'dixie','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What did you want to be when you grew up?",','I don''t know','dixie.pearson@gmail.com',0, '12.png','employee'),
       (13,'marjorie','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What do you remember most from your childhood?",','I don''t know','marjorie.stevenson@gmail.com',0,'13.png','employee'),
       (14,'tabitha','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What is the name of the boy or girl that you first kissed?",','I don''t know','tabitha.kennedy@gmail.com',0,'14.png','employee'),
       (15,'jonathan','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What is the name of the first school you attended?",','I don''t know','jonathan.sims@gmail.com',0,'15.png','employee'),
       (16,'stuart','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What is the name of your favorite childhood friend?",','I don''t know','stuart.white@gmail.com',0,'16.png','employee'),
       (17,'jaime','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What is the name of your first pet?",','I don''t know','jaime.murphy@gmail.com',0,'17.png','employee'),
       (18,'mattie','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What is your mother''s maiden name?",','I don''t know','mattie.allen@gmail.com',0,'18.png','employee'),
       (19,'warren','sha1:64000:18:1sU7jfRgP+0BkcKoqACWp5sI9byAHqBB:7bMFTqeuWBshTHXyviEdM3xH','"What was your favorite place to visit as a child?"','I don''t know','warren.oliver@gmail.com',0,'19.png','employee');

INSERT INTO BasicInfo (id,   first_name,   middle_name,   last_name,   summary)
VALUES (1, 'Yoseph Berhanu', null, 'Alebachew', 'Test'),
       (2, 'Sai Nikhita', null, 'Nayani', 'Test'),
       (3, 'Hyndavi', null, 'Venkatreddygari', 'Committed English major with exceptional research and writing abilities. Awarded multiple honors based on merits and expected to graduate from UCLA in June of 2019. Tutored freshmen and improved writing scores by 40%.'),
       (10,'Lynne','Jaime','Simpson','High-achieving college student majoring in Finance with a 3.8 GPA and a consistent presence on the Dean’s List for three consecutive semesters. Eager to apply my financial acumen and academic excellence to a dynamic entry-level role. Adept at financial analysis, data interpretation, and modeling, with a keen eye for detail.'),
       (11,'Brittany','Jonathan','Tyler','Seasoned marketing professional with over 10 years of experience, including 5 years in management roles. Led and motivated teams to execute successful marketing strategies, resulting in a 30% increase in brand visibility through a targeted SEO and content marketing campaign, and generating $2.5 million in additional revenue through successful email marketing initiatives. Proven skills in market research, campaign management, and cross-functional collaboration, eager to apply leadership and strategic expertise in a dynamic marketing management role.'),
       (12,'Dixie','Warren','Pearson','Certified Legal Assistant excited at the prospect of managing case files and analyzing legal documents in a professional setting. Proactive worker recognized by peers for my effective organizational and writing skills. Looking to apply my understanding of litigation support software and case analysis software at your firm.'),
       (13,'Marjorie','Tabitha','Stevenson','Passionate childcare professional with over a year of babysitting experience. Skilled at creating engaging, educational activities for children and fostering a safe, caring environment. Employed unique tutoring methods to help the child I was babysitting improve her speech impediment. CPR certified and an experienced vegetarian home cook.'),
       (14,'Tabitha','Jonathan','Kennedy','Recent Computer Science graduate (3.8 GPA) seeking to use my backend development experience in an entry-level position. Possess 4 months of internship experience building and testing applications for Android, iOS, and Windows. Skilled with C, C++, Java, JavaScript, Python, and Swift. Independently built a note-taking application that was recently approved by the Apple App Store.'),
       (15,'Jonathan','Stuart','Sims','Passionate childcare professional with over a year of babysitting experience. Skilled at creating engaging, educational activities for children and fostering a safe, caring environment. Employed unique tutoring methods to help the child I was babysitting improve her speech impediment. CPR certified and an experienced vegetarian home cook.'),
       (16,'Stuart','Marjorie','White','Committed English major with exceptional research and writing abilities. Awarded multiple honors based on merits and expected to graduate from UCLA in June of 2019. Tutored freshmen and improved writing scores by 40%.'),
       (17,'Jaime','Marjorie','Murphy','Recent Marketing graduate with 5 months of internship experience in digital marketing and branding in the fashion industry. Helped launch a branded account on Tik Tok for Michelle Textiles, gaining over 8,000 followers in 4 months. Skilled in growing engagement and staying on top of the latest trends to improve brand growth.'),
       (18,'Mattie','Lynne','Allen','Enthusiastic high school student with strong interpersonal skills and perfect attendance record. Was a top performer in my school’s fundraiser last year, selling $450 worth of chocolate. Have CPR certification and am well-versed in food safety techniques from helping in my grandparents’ restaurant.'),
       (19,'Warren','Tabitha','Oliver','Diligent high school graduate with a 3.9/4.0 GPA and 100% attendance record. Served a term as student council president with 70% support from council members and organized a charity drive for homeless veterans, raising $4,700.');
INSERT INTO Address (id, address1, city, state_name, zipcode, country, email, phone_number)
VALUES (1, '1407 Toms Creek Rd', 'Blacksburg', 'Virginia', '24060', 'United States of America', 'yosephcs@gmail.com', '+16696994955'),
       (10,'26 Baker Lane','Davison','Michigan','48423','United States of America','simpson@vtl.edu','(206) 342-8631'),
       (11,'492 South Gates Street','Saint Albans','New York','11412','United States of America','tyler@vtl.edu','(717) 550-1675'),
       (12,'4 Greystone Ave.','Winter Haven','Florida','33880','United States of America','pearson@vtl.edu','(248) 762-0356'),
       (13,'8115 W. Sutor Street','Romulus','Michigan','48174','United States of America','stevenson@vtl.edu','(253) 644-2182'),
       (14,'87 Smith Store Ave.','Fremont','Ohio','43420','United States of America','kennedy@vtl.edu','(212) 658-3916'),
       (15,'234 8th Ave.','Powhatan','Virginia','23139','United States of America','sims@vtl.edu','(209) 300-2557'),
       (16,'550 Lakewood Drive','Erie','Pennsylvania','16506','United States of America','white@vtl.edu','(262) 162-1585'),
       (17,'59 S. Colonial Lane','Jamaica Plain','Massachusetts','2130','United States of America','murphy@vtl.edu','(252) 258-3799'),
       (18,'152 Grant Road','Glen Ellyn','Illinois','60137','United States of America','allen@vtl.edu','(234) 109-6666'),
       (19,'63 West Front Street','Park Ridge','Illinois','60068','United States of America','oliver@vtl.edu','(201) 874-8593');
INSERT INTO Skill (user, name)
VALUES (1, 'Java'), (1, 'JavaScript'), (1, 'HTML'), (2, 'Java'), (2, 'Jakarta EE'), (2, 'Database'), (10,'.NET'), (10,'CSS'),
       (10,'Django'), (10,'Flask'), (10,'HTML'), (10,'J2EE'), (10,'MySQL'), (10,'PostgreSQL'), (11,'Angular'), (11,'C++'),
       (11,'JavaScript'), (11,'MongoDB'),(11,'Node'),(11,'Python'),(11,'Redux'), (11,'SQL'),(12,'Angular'), (12,'C#'),
       (12,'Flask'),(12,'HTML'),(12,'JavaScript'),(12,'MongoDB'),(12,'Redux'),(12,'TypeScript'),(13,'C#'),(13,'Flask'),
       (13,'J2EE'), (13,'MongoDB'),(13,'Node'),(13,'Python'),(13,'React'),(13,'TypeScript'),(14,'.NET'),(14,'C#'),
       (14,'Django'),(14,'Express'),(14,'J2EE'),(14,'Java'),(14,'MySQL'),(14,'Python'),(15,'.NET'),(15,'Angular'),
       (15,'CSS'),(15,'Django'),(15,'Node'),(15,'Redux'),(15,'SQL'),(15,'TypeScript'),(16,'CSS'),(16,'Django'),
       (16,'Flask'),(16,'J2EE'),(16,'Java'),(16,'MongoDB'),(16,'Python'),(16,'TypeScript'),(17,'Angular'),(17,'C++'),
       (17,'Django'),(17,'Flask'),(17,'JavaScript'),(17,'MySQL'),(17,'PostgreSQL'),(17,'Redux'),(18,'.NET'),(18,'C#'),
       (18,'Django'),(18,'Flask'),(18,'J2EE'),(18,'JavaScript'),(18,'React'),(18,'SpringBoot'),(19,'.NET'),(19,'C#'),
       (19,'Flask'),(19,'Node'),(19,'PostgreSQL'),(19,'Python'),(19,'Redux'),(19,'SpringBoot'),(18,'C++'),(17,'Express'),
       (17,'Java'),(10,'JavaScript'),(17,'MongoDB'),(17,'Python'),(17,'React'),(17,'SpringBoot');
INSERT INTO Education (user, instituteName, degree, startDate, graduationDate, description)
VALUES (1, 'Addis Ababa University', 'MSc. in CS', '2010-11-06', '2014-11-23', ''),
       (1, 'Virginia Tech', 'Phd in CS', '2023-06-06', '2028-07-19', ''),
       (1, 'Addis Ababa University', 'BSc. in CS', '2007-11-07', '2010-11-07', ''),
       (10,'Boston College','B.A. in Sociology','1971-12-08','1977-02-20',''),
       (10,'Williams College','M.A. in Social Transformation','1972-02-23','1977-05-08',''),
       (11,'University of Pennsylvania','B.Sc. in Architecture ','1973-09-03','1978-11-17',''),
       (11,'Tufts University','M.Sc. in Architecture and Design','1974-09-04','1979-11-18',''),
       (12,'University of Washington','B.A. in Economics','1976-08-12','1981-10-26',''),
       (12,'Colgate University','M.A. in Macroeconomics','1978-01-06','1983-03-22',''),
       (13,'Lafayette College','B.A. in Psychology ','1978-09-29','1983-12-13',''),
       (13,'Massachusetts Institute of Technology','Ph.D. in Educational Psychology','1978-11-08','1984-01-22',''),
       (14,'Pomona College','B.A. in Public Administration ','1979-01-17','1984-04-01',''),
       (14,'Harvey Mudd College','M.A. in Public Policy : Food','1980-03-04','1985-05-18',''),
       (15,'George Washington University','B.A. in Philosophy ','1984-06-15','1989-08-29',''),
       (15,'Wesleyan University','M.A. in Metaphysics ','1986-11-13','1992-01-27',''),
       (15,'Duke University','Ph.D. in Philosophy Of Action','1989-07-06','1994-09-19',''),
       (16,'Franklin and Marshall College','B.A. in Political Science ','1989-09-19','1994-12-03',''),
       (16,'California Institute of Technology','M.A. in Public Policy','1990-06-01','1995-08-15',''),
       (17,'Boston College','B.Sc. in Computer Sciences ','2000-04-24','2005-07-08',''),
       (17,'United States Military Academy','M.Sc. in Artificial Intelligence','2000-08-15','2005-10-29',''),
       (17,'Colorado College','Ph.D. in Cognitive Science -- Natural Language Processing (Computational Linguistics)','2000-08-25','2005-11-08',''),
       (18,'Boston University','B.Sc. in Computer Sciences ','2005-04-13','2010-06-27',''),
       (18,'Cooper Union','PhD. in Computer Security And Reliability : Fault-tolerant Computing','2012-11-13','2018-01-27',''),
       (19,'University of Chicago','B.A. in Public Administration ','2013-01-11','2018-03-27',''),
       (19,'University of Michigan, Ann Arbor','M.A. in Public Policy : Food','2016-11-10','2022-01-24',''),
       (17,'University of Maryland, College Park','B.A. in Philosophy ','2020-01-21','2025-04-05',''),
       (17,'University of North Carolina, Chapel Hill','M.A. in Metaphysics ','2022-01-26','2027-04-11','');
INSERT INTO Certificate (user, name, dateObtained)
VALUES  (1,'AWS Certified Cloud Practitioner (CCP)','2005-07-08'),
        (1,'Certified Ethical Hacker (CEH)','2005-10-29'),
        (1,'Certified ScrumMaster (CSM)','2005-11-08'),
        (1,'Cisco Certified Network Associate (CCNA)','2010-06-27'),
        (1,'Cisco Certified Technician (CCT)','2018-01-27'),
        (1,'CompTIA certifications','2018-03-27'),
        (1,'GIAC Information Security Fundamentals (GISF)','2022-01-24'),
        (10,'AWS Certified Cloud Practitioner (CCP)','2005-07-08'),
        (11,'Certified Ethical Hacker (CEH)','2005-10-29'),
        (12,'Certified ScrumMaster (CSM)','2005-11-08'),
        (13,'Cisco Certified Network Associate (CCNA)','2010-06-27'),
        (14,'Cisco Certified Technician (CCT)','2018-01-27'),
        (15,'CompTIA certifications','2018-03-27'),
        (16,'GIAC Information Security Fundamentals (GISF)','2022-01-24'),
        (17,'ISACA Information Technology Certified Associate (ITCA)','2025-04-05'),
        (18,'ITIL 4 Foundation','2027-04-11'),
        (19,'Linux Essentials Certification','2005-07-08'),
        (11,'Microsoft Certified: Fundamentals','2005-10-29'),
        (10,'Microsoft Technology Associate (MTA)','2005-11-08'),
        (11,'Oracle Certified Associate (OCA)','2010-06-27'),
        (12,'PMI Certified Associate in Project Management (CAPM)','2018-01-27'),
        (14,'Systems Security Certified Practitioner','2018-03-27'),
        (15,'AWS Certified Cloud Practitioner (CCP)','2022-01-24'),
        (12,'Certified Ethical Hacker (CEH)','2025-04-05'),
        (19,'Certified ScrumMaster (CSM)','2027-04-11'),
        (16,'Cisco Certified Network Associate (CCNA)','2005-07-08'),
        (10,'Cisco Certified Technician (CCT)','2005-10-29'),
        (12,'CompTIA certifications','2005-11-08'),
        (12,'GIAC Information Security Fundamentals (GISF)','2010-06-27'),
        (14,'ISACA Information Technology Certified Associate (ITCA)','2018-01-27'),
        (16,'ITIL 4 Foundation','2018-03-27'),
        (10,'Linux Essentials Certification','2022-01-24'),
        (19,'Microsoft Certified: Fundamentals','2025-04-05'),
        (17,'Microsoft Technology Associate (MTA)','2027-04-11'),
        (17,'Oracle Certified Associate (OCA)','2005-07-08'),
        (16,'PMI Certified Associate in Project Management (CAPM)','2005-10-29'),
        (12,'Systems Security Certified Practitioner','2005-11-08');
INSERT INTO Project (manager, name, client, description, proposal_date, status)
VALUES (7, 'Affronting everything discretion men now own did', 'Virginia Tech', 'Do so written as raising parlors spirits mr elderly. Made late in of high left hold. Carried females of up highest calling. Limits marked led silent dining her she far. Sir but elegance marriage dwelling likewise position old pleasure men. Dissimilar themselves simplicity no of contrasted as. Delay great day hours men. Stuff front to do allow to asked he. Barton did feebly change man she afford square add. Want eyes by neat so just must. Past draw tall up face show rent oh mr. Required is debating extended wondered as do. New get described applauded incommode shameless out extremity but. Resembled at perpetual no believing is otherwise sportsman. Is do he dispatched cultivated travelling astonished. Melancholy am considered possession on collecting everything. If wandered relation no surprise of screened doubtful. Overcame no insisted ye of trifling husbands. Might am order hours on found. Or dissimilar companions friendship impossible at diminution. Did yourself carriage learning she man its replying. Sister piqued living her you enable mrs off spirit really. Parish oppose repair is me misery. Quick may saw style after money mrs. ', '2023-11-05', 1),
       (7, 'Still round match we to', 'IBM', 'Frankness pronounce daughters remainder extensive has but. Happiness cordially one determine concluded fat. Plenty season beyond by hardly giving of. Consulted or acuteness dejection an smallness if. Outward general passage another as it. Very his are come man walk one next. Delighted prevailed supported too not remainder perpetual who furnished. Nay affronting bed projection compliment instrument. Able an hope of body. Any nay shyness article matters own removal nothing his forming. Gay own additions education satisfied the perpetual. If he cause manor happy. Without farther she exposed saw man led. Along on happy could cease green oh. Delightful remarkably mr on announcing themselves entreaties favourable. About to in so terms voice at. Equal an would is found seems of. The particular friendship one sufficient terminated frequently themselves. It more shed went up is roof if loud case. Delay music in lived noise an. Beyond genius really enough passed is up. ', '2024-01-15', 1),
       (7, 'Me mean able my by in they', 'Google', 'On projection apartments unsatiable so if he entreaties appearance. Rose you wife how set lady half wish. Hard sing an in true felt. Welcomed stronger if steepest ecstatic an suitable finished of oh. Entered at excited at forming between so produce. Chicken unknown besides attacks gay compact out you. Continuing no simplicity no favourable on reasonably melancholy estimating. Own hence views two ask right whole ten seems. What near kept met call old west dine. Our announcing sufficient why pianoforte.', '2023-12-25', 1);
INSERT INTO ProjectEmployee (employee, project, position, dateAdded)
VALUES (1, 1, 'Programmer', '2010-11-06'),
       (2, 1, 'QA', '2023-11-06'),
       (10, 1, 'QA', '2023-11-06'),
       (11, 1, 'QA', '2023-11-06'),
       (12, 1, 'QA', '2023-11-06'),
       (13, 1, 'QA', '2023-11-06'),
       (14, 1, 'QA', '2023-11-06'),
       (10, 2, 'QA', '2023-11-06'),
       (11, 2, 'QA', '2023-11-06'),
       (12, 2, 'QA', '2023-11-06'),
       (13, 2, 'QA', '2023-11-06'),
       (14, 2, 'QA', '2023-11-06');
INSERT INTO Configuration ( config_key, name , value)
VALUES ('suspendcount','Number of unsuccessful login attempts before account is suspended', '5'),
       ('suspendtime','How long an account should be suspended after unsuccessful login attempts (in mins)', '3'),
        ('recaptchacount','How many unsuccessful login attempts to wait before displaying reCAPTCHA challenge is displayed ', '2'),
        ('maximumuploadcount','Maximum number of attachments a user can have','3'),
        ('maximumUploadSize','Maximum size of a file that can be uploaded in MB','2');

INSERT INTO Template (name, content)
VALUES ('SRT', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>

	<title>${profile.fullName} | ${profile.fullName}</title>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />

	<meta name="keywords" content="" />
	<meta name="description" content="" />

	<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.7.0/build/reset-fonts-grids/reset-fonts-grids.css" media="all" />
	<style type="text/css">
		.msg { padding: 10px; background: #222; position: relative; }
		.msg h1 { color: #fff;  }
		.msg a { margin-left: 20px; background: #408814; color: white; padding: 4px 8px; text-decoration: none; }
		.msg a:hover { background: #266400; }

		/* //-- yui-grids style overrides -- */
		body { font-family: Georgia; color: #444; }
		#inner { padding: 10px 80px; margin: 80px auto; background: #f5f5f5; border: solid #666; border-width: 8px 0 2px 0; }
		.yui-gf { margin-bottom: 2em; padding-bottom: 2em; border-bottom: 1px solid #ccc; }

		/* //-- header, body, footer -- */
		#hd { margin: 2.5em 0 3em 0; padding-bottom: 1.5em; border-bottom: 1px solid #ccc }
		#hd h2 { text-transform: uppercase; letter-spacing: 2px; }
		#bd, #ft { margin-bottom: 2em; }

		/* //-- footer -- */
		#ft { padding: 1em 0 5em 0; font-size: 92%; border-top: 1px solid #ccc; text-align: center; }
		#ft p { margin-bottom: 0; text-align: center;   }

		/* //-- core typography and style -- */
		#hd h1 { font-size: 48px; text-transform: uppercase; letter-spacing: 3px; }
		h2 { font-size: 152% }
		h3, h4 { font-size: 122%; }
		h1, h2, h3, h4 { color: #333; }
		p { font-size: 100%; line-height: 18px; padding-right: 3em; }
		a { color: #990003 }
		a:hover { text-decoration: none; }
		strong { font-weight: bold; }
		li { line-height: 24px; border-bottom: 1px solid #ccc; }
		p.enlarge { font-size: 144%; padding-right: 6.5em; line-height: 24px; }
		p.enlarge span { color: #000 }
		.contact-info { margin-top: 7px; }
		.first h2 { font-style: italic; }
		.last { border-bottom: 0 }


		/* //-- section styles -- */

		a#pdf { display: block; float: left; background: #666; color: white; padding: 6px 50px 6px 12px; margin-bottom: 6px; text-decoration: none;  }
		a#pdf:hover { background: #222; }

		.job { position: relative; margin-bottom: 1em; padding-bottom: 1em; border-bottom: 1px solid #ccc; }
		.job h4 { position: absolute; top: 0.35em; right: 0 }
		.job p { margin: 0.75em 0 3em 0; }

		.last { border: none; }
		.skills-list {  }
		.skills-list ul { margin: 0; }
		.skills-list li { margin: 3px 0; padding: 3px 0; }
		.skills-list li span { font-size: 152%; display: block; margin-bottom: -2px; padding: 0 }
		.talent { width: 32%; float: left }
		.talent h2 { margin-bottom: 6px; }

		#srt-ttab { margin-bottom: 100px; text-align: center;  }
		#srt-ttab img.last { margin-top: 20px }

		/* --// override to force 1/8th width grids -- */
		.yui-gf .yui-u{width:80.2%;}
		.yui-gf div.first{width:12.3%;}

	</style>
</head>
<body>
<div id="doc2" class="yui-t7">
	<div id="inner">

		<div id="hd">
			<div class="yui-gc">
				<div class="yui-u first">
					<h1>${profile.fullName}</h1>
				</div>

				<div class="yui-u">
					<div class="contact-info">
						<br/><br/> <br/>
						<h3><a href="${profile.address.email}">${profile.address.email}</a></h3>
						<h3>${profile.address.phoneNumber}</h3>
					</div><!--// .contact-info -->
				</div>
			</div><!--// .yui-gc -->
		</div><!--// hd -->
		<div id="bd">
			<div id="yui-main">
				<div class="yui-b">
					<div class="yui-gf">
						<div class="yui-u first">
							<h2>Summary</h2>
						</div>
						<div class="yui-u">
							<p class="enlarge">
								${profile.basicInfo.summary}
							</p>
						</div>
					</div><!--// .yui-gf -->
					<div class="yui-gf">
						<div class="yui-u first">
							<h2>Skills</h2>
						</div>
						<div class="yui-u">
							<#list profile.skills>
							<ul class="talent">
								<#items as skill>
									<#if skill?counter%3 == 0 & !(skill?has_next)>
								  		<li class="last">${skill.name}</li>
								  		</ul> <ul class="talent">
									<#else>
									  <li>${skill.name}</li>
									</#if>
								</#items>
							</ul>
							</#list>
						</div>
					</div><!--// .yui-gf-->
					<div class="yui-gf">
						<div class="yui-u first">
							<h2>Experience</h2>
						</div><!--// .yui-u -->
						<div class="yui-u">
							<#list profile.experiences>
								<#items as experience>
									<#if !(experience?has_next)>
								  		<div class="job last">
									<#else>
										<div class="job" >
									</#if>
										<h2>${experience.organization}</h2>
										<h3>${experience.role}</h3>
										<h4>${experience.startDate?string["MMM  yyyy"]}-${experience.endDate?string["MMM  yyyy"]}</h4>
										<p>${experience.responsibilities}</p>
									</div>
								</#items>
							</#list>
						</div><!--// .yui-u -->
					</div><!--// .yui-gf -->
					<div class="yui-gf last">
						<div class="yui-u first">
							<h2>Education</h2>
						</div>
						<div class="yui-u">
							<#list profile.educations>
								<#items as education>
									<h2>${education.instituteName}</h2>
									<h3>${education.degree} &mdash; <strong>${education.graduationDate?string.yyyy}</strong> </h3>
									<br/>
								</#items>
							</#list>
						</div>
					</div><!--// .yui-gf -->
				</div><!--// .yui-b -->
			</div><!--// yui-main -->
		</div><!--// bd -->
		<div id="ft">
			<p>${profile.fullName} &mdash; <a href="mailto:${profile.address.email}">${profile.address.email}</a> &mdash;${profile.address.phoneNumber}</p>
		</div><!--// footer -->
	</div><!-- // inner -->
</div><!--// doc -->
</body>
</html>'),
       ('Simple', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
     <title>Resume</title>
     <style type="text/css">
        * { margin: 0; padding: 0; }
        body { font: 16px Helvetica, Sans-Serif; line-height: 24px; background: url(images/noise.jpg); }
        .clear { clear: both; }
        #page-wrap { width: 800px; margin: 40px auto 60px; }
        #pic { float: right; margin: -30px 0 0 0; }
        h1 { margin: 0 0 16px 0; padding: 0 0 16px 0; font-size: 42px; font-weight: bold; letter-spacing: -2px; border-bottom: 1px solid #999; }
        h2 { font-size: 20px; margin: 0 0 6px 0; position: relative; }
        h2 span { position: absolute; bottom: 0; right: 0; font-style: italic; font-family: Georgia, Serif; font-size: 16px; color: #999; font-weight: normal; }
        p { margin: 0 0 16px 0; }
        a { color: #999; text-decoration: none; border-bottom: 1px dotted #999; }
        a:hover { border-bottom-style: solid; color: black; }
        ul { margin: 0 0 32px 17px; }
        #objective { width: 500px; float: left; }
        #objective p { font-family: Georgia, Serif; font-style: italic; color: #666; }
        dt { font-style: italic; font-weight: bold; font-size: 18px; text-align: right; padding: 0 26px 0 0; width: 150px; float: left; height: 100px; border-right: 1px solid #999;  }
        dd { width: 600px; float: right; }
        dd.clear { float: none; margin: 0; height: 15px; }
     </style>
</head>
<body>
    <div id="page-wrap">
        <img src="${profile.profilePicture}" style="width:10em" alt="Photo of ${profile.fullName}" id="pic" />
        <div id="contact-info" class="vcard">
            <!-- Microformats! -->
            <h1 class="fn">${profile.fullName}</h1>
            <p>
                Cell: <span class="tel">${profile.address.phoneNumber}</span><br />
                Email: <a class="email" href="mailto:${profile.address.email}">${profile.address.email}</a>
            </p>
        </div>
        <div id="objective">
            <p>${profile.summary}</p>
        </div>
        <div class="clear"></div>
        <dl>
            <dd class="clear"></dd>
            <dt>Education</dt>
            <#if profile.educations?has_content>
              <#list profile.educations as education>
              <dd>
                  <h2>${education.instituteName}, ${education.graduationDate}</h2>
                  <p><strong>Major:</strong> ${education.degree}<br />
              </dd>
              </#list>
            </#if>
            <dd class="clear"></dd>
            <#if profile.skills?has_content>
              <dt>Skills</dt>
              <dd>
                <#list profile.skills as skill>
                  <h2>${skill.name}</h2>
                </#list>
              </dd>
            </#if>
            <dd class="clear"></dd>
            <dt>Experience</dt>
            <dd>
              <#if profile.experiences?has_content>
                <#list profile.experiences as experience>
                  <h2>
                    ${experience.organization}<span>${experience.title} - ${experience.startDate} - ${experience.endDate}</span>
                  </h2>
                  <ul>
                    <li>${experience.responsibilities}</li>
                  </ul>
              </#list>
            </#if>
            </dd>
            <dd class="clear"></dd>
            <dt>References</dt>
            <dd>Available on request</dd>
            <dd class="clear"></dd>
        </dl>
        <div class="clear"></div>
    </div>
</body>
</html>'),
       ('Random','<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="theme-color" content="#fdfdff" />
    <title>${profile.fullName}''s Resume</title>
    <style type="text/css">
      * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
        }
        html {
          height: 100%;
        }

        body {
          min-height: 100%;
          background: #eee;
          font-family: "Lato", sans-serif;
          font-weight: 400;
          color: #222;
          font-size: 14px;
          line-height: 26px;
          padding-bottom: 50px;
        }

        .container {
          max-width: 700px;
          background: #fff;
          margin: 0px auto 0px;
          box-shadow: 1px 1px 2px #DAD7D7;
          border-radius: 3px;
          padding: 40px;
          margin-top: 50px;
        }

        .header {
          margin-bottom: 30px;

          .full-name {
            font-size: 40px;
            text-transform: uppercase;
            margin-bottom: 5px;
          }

          .first-name {
            font-weight: 700;
          }

          .last-name {
            font-weight: 300;
          }

          .contact-info {
            margin-bottom: 20px;
          }

          .email ,
          .phone {
            color: #999;
            font-weight: 300;
          }

          .separator {
            height: 10px;
            display: inline-block;
            border-left: 2px solid #999;
            margin: 0px 10px;
          }

          .position {
            font-weight: bold;
            display: inline-block;
            margin-right: 10px;
            text-decoration: underline;
          }
        }


        .details {
          line-height: 20px;

          .section {
            margin-bottom: 40px;
          }

          .section:last-of-type {
            margin-bottom: 0px;
          }

          .section__title {
            letter-spacing: 2px;
            color: #54AFE4;
            font-weight: bold;
            margin-bottom: 10px;
            text-transform: uppercase;
          }

          .section__list-item {
            margin-bottom: 40px;
          }

          .section__list-item:last-of-type {
            margin-bottom: 0;
          }

          .left ,
          .right {
            vertical-align: top;
            display: inline-block;
          }

          .left {
            width: 60%;
          }

          .right {
            tex-align: right;
            width: 39%;
          }

          .name {
            font-weight: bold;
          }

          a {
            text-decoration: none;
            color: #000;
            font-style: italic;
          }

          a:hover {
            text-decoration: underline;
            color: #000;
          }

          .skills {

          }

          .skills__item {
            margin-bottom: 10px;
          }

          .skills__item .right {
            input {
              display: none;
            }

            label {
              display: inline-block;
              width: 20px;
              height: 20px;
              background: #C3DEF3;
              border-radius: 20px;
              margin-right: 3px;
            }

            input:checked + label {
              background: #79A9CE;
            }
          }
        }
    </style>
  </head>
  <body>
    <main>
      <link href="https://fonts.googleapis.com/css?family=Lato:400,300,700" rel="stylesheet" type="text/css">
      <div class="container">
        <div class="header">
          <div class="full-name">
            <span class="first-name">${profile.basicInfo.firstName}</span>
            <span class="last-name">${profile.basicInfo.lastName}</span>
          </div>
          <div class="contact-info">
            <span class="email">Email: </span>
            <span class="email-val">${profile.address.email}</span>
            <span class="separator"></span>
            <span class="phone">Phone: </span>
            <span class="phone-val">${profile.address.phoneNumber}</span>
          </div>
          <div class="about">
            <span class="desc">
              ${profile.basicInfo.summary}
            </span>
          </div>
        </div>
        <div class="details">
          <#if profile.experiences?has_content>
            <#list profile.experiences as experience>
              <div class="section">
                <div class="section__title">Experience</div>
                <div class="section__list">
                  <div class="section__list-item">
                    <div class="left">
                      <div class="name">${experience.organization}</div>
                      <div class="duration">${experience.startDate} - ${experience.endDate}</div>
                    </div>
                    <div class="right">
                      <div class="name">${experience.title}</div>
                      <div class="desc">${experience.responsibilities}</div>
                    </div>
                  </div>
                </div>
              </div>
            </#list>
          </#if>
          <#if profile.educations?has_content>
            <#list profile.educations as education>
              <div class="section">
                <div class="section__title">Education</div>
                <div class="section__list">
                  <div class="section__list-item">
                    <div class="left">
                      <div class="name">${education.instituteName}</div>
                      <div class="duration">${education.startDate} - ${education.graduationDate}</div>
                    </div>
                    <div class="right">
                      <div class="name">${education.degree}</div>
                      <div class="desc">${education.description}</div>
                    </div>
                  </div>
                </div>
              </div>
            </#list>
          </#if>
          <#if profile.skills?has_content>
              <div class="section">
                <div class="section__title">Skills</div>
                <div class="skills">
                  <#list profile.skills as skill>
                    <div class="skills__item">
                      <div class="left">
                        <div class="name">
                          ${skill.name}
                        </div>
                      </div>
                    </div>
                  </#list>
                </div>
              </div>
          </#if>
        </div>
      </div>
    </main>
  </body>
</html>');