#!/bin/bash

sm --start MONGO DATASTREAM 

sm --start INTEGRATION_CATALOGUE INTEGRATION_CATALOGUE_ADMIN_API EMAIL HMRC_EMAIL_RENDERER MAILGUN_STUB

./run_local.sh
