#!/bin/bash

sm --start ASSETS_FRONTEND -r 3.11.0

sm --start MONGO DATASTREAM 

sm --start INTEGRATION_CATALOGUE INTEGRATION_CATALOGUE_ADMIN_API

./run_local.sh
