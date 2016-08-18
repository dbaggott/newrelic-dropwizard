# New Relic Dropwizard Plugin [![Build Status](https://travis-ci.org/dbaggott/newrelic-dropwizard.svg?branch=master)](https://travis-ci.org/dbaggott/newrelic-dropwizard) [![codecov.io](https://codecov.io/github/dbaggott/newrelic-dropwizard/coverage.svg?branch=master)](https://codecov.io/github/dbaggott/newrelic-dropwizard?branch=master)

A [New Relic (NR)](https://newrelic.com/plugins/oo/487) plugin agent for monitoring the health and performance of one or more
[Dropwizard](http://dropwizard.io/) applications over http using the applications' `/healthcheck` and `/metrics` 
administrative endpoints.

This plugin allows you to easily receive alerts via NR whenever your Dropwizard application enters into an unhealthy 
state.  Additionally, you can optionally configure NR to send alerts whenever the rate of 4xx responses, 5xx responses, 
logged errors, and/or JVM heap utilization exceeds configured thresholds.  Thresholds can be configured on a per 
application basis within NR.

The NR dashboard for this plugin provides historical views of your application's health and performance so you can 
easily see what went wrong and when.

Multiple Dropwizard applications can be monitored from the same plugin instance.  The plugin polls all configured 
Dropwizard applications every minute and publishes the appropriate data into NR.  The plugin handles failures to connect 
to or receive a response from a Dropwizard application.

## Health Check Monitoring

This plugin provides the ability to send NR alerts based on the Dropwizard application's `/healthcheck` response and 
records historical data within NR on which health check(s) failed.  The overall status of the `/healthcheck` response, 
the status code returned, and the states of the individual health checks are recorded.

Since NR does not support any text values or annotation of metric values, it's impossible to capture health check
messages within NR.

## Metrics Data Collection

A wide-range of the standard Dropwizard metrics as exposed on `/metrics` are collected and displayed within the NR
dashboard including (among other things) information about requests rates and response times, request queueing, 
threading, and memory. 

## New Relic Plugin Dashboard UI

<img src="https://cloud.githubusercontent.com/assets/112677/14356368/75ba0a66-fc99-11e5-894f-d7c16275036e.png" width="90%"></img> <img src="https://cloud.githubusercontent.com/assets/112677/14356789/13d6acd0-fc9b-11e5-83b7-891bc2683288.png" width="90%"></img> <img src="https://cloud.githubusercontent.com/assets/112677/14356393/9427fc7e-fc99-11e5-9e48-0ef1edc31c6d.png" width="90%"></img> <img src="https://cloud.githubusercontent.com/assets/112677/14356413/a14b043c-fc99-11e5-9b45-c6f0017d62ee.png" width="90%"></img> <img src="https://cloud.githubusercontent.com/assets/112677/14356440/c06ba79a-fc99-11e5-83fe-37744e2f9167.png" width="90%"></img> <img src="https://cloud.githubusercontent.com/assets/112677/14356427/b10950cc-fc99-11e5-964d-230e5898eef4.png" width="90%"></img> 

## Installation

Pre-requisites: java (>= 6)

This plugin is available in [Plugin Central](http://newrelic.com/plugins/iodnbg/487) and is NPI-compliant and can be 
[installed using NPI](https://docs.newrelic.com/docs/plugins/plugins-new-relic/installing-plugins/installing-npi-compatible-plugin). 

`npi install io.dnbg.newrelic.dropwizard`

Additionally, New Relic has [documentation for doing it the Chef or Puppet way]
(https://docs.newrelic.com/docs/plugins/plugins-new-relic/installing-plugins/plugin-installation-chef-and-puppet).

## Dropwizard Version Support

The agent has been tested with Dropwizard version 0.7.1 through 0.9.2.  However, some older versions of Dropwizard
are missing metrics that are reported by this plugin.  In those cases, the corresponding graphs or data points will
be blank within NR.

Relevant Dropwizard `/metrics` changes by version:

* 0.8.0: adds `org.eclipse.jetty.util.thread.QueuedThreadPool.dw.utilization-max` to provide utilization of the thread
pool relative to the max allowed threads
* 0.8.1: adds 4xx and 5xx responses reported as a percentage of all requests (e.g. `io.dropwizard.jetty.MutableServletContextHandler.percent-4xx-1m`)

The code is careful to be fault tolerant to maximize the likelihood that it is forward compatible with not-yet-released 
versions of Metrics/Dropwizard and upgrades to DW should never break data collection.  At worse, data will be missing.

## Healthcheck Implementation Fine Print

The New Relic Platform does not natively support boolean metric values and this plugin represents boolean values using 
0 (false) and 1 (true).  Conceptually, [this is somewhat problematic]
(https://discuss.newrelic.com/t/how-to-report-yes-no-boolean-values-via-a-custom-plugin/4990/6) but, in practice, it 
mostly works.  Because the boolean values are treated as numbers and subject to aggregation, the NR dashboard UI can 
occasionally report unexpected "boolean" values that are fractional.  I have not seen this lead to false alerting but 
it can be confusing if you're not aware of it.

Additionally, NR's current support for alerting on Plugin data is limited.  Specifically, it only supports sending 
notifications when the value of some metric exceeds a specified threshold.  Alerting on values of 0 (or on the absence 
of data) is not possible.

The approach this plugin takes is to publish an "unhealthiness" metric as a boolean value into NR and to then alert when 
the threshold of the "unhealthiness" metric is greater than some very small value (eg 0.001).  In practice, this 
delivers what you want: NR sends an alert whenever the application is unhealthy -- as with most NR alerts, there's a
several minute lag after the application recovers before NR recovers to prevent excessive alerting in the case of a
flagging metric.

In addition to the overall "unhealthiness" metric, the plugin publishes individual "healthiness" metrics and a http status 
code metric for visualization within the UI.
