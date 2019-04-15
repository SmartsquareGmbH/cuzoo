ALTER TABLE OPPORTUNITY
    ADD last_progress TIMESTAMP;

UPDATE OPPORTUNITY
SET last_progress = (SELECT date from CONTACT_POINT where opportunity_id = OPPORTUNITY.ID order by date desc limit 1);