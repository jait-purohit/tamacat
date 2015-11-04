# Release Notes #

## Release 1.0.6 (2013-07-01) ##

---

  * The ReverseProxyHandler and WorkerThread support keep-alive connection. (Bugfix)
  * The keepAliveTimeout property was added to ReverseProxyHandler. (default:30000 msec.)

## Release 1.0.5 (2013-05-14) ##

---

  * Security update
  * We advise users of previous versions to upgrade.

## Release 1.0.4 (2013-04-25) ##

---

  * The highlights in the tamacat-httpd-1.0.4 are the following:
  * The ReverseProxyHandler and WorkerThread support keep-alive connection.
  * [Issue 7](https://code.google.com/p/tamacat/issues/detail?id=7): The Reverse proxy fault correction from which a response status line is set to HTTP/1.1 in the case of HTTP/1.0.
  * Use HttpCore-4.2.4/HttpClient-4.2.5

## Release 1.0.3 (2013-01-21) ##

---

  * This is a maintenance release that fixes a number of bugs.(Issue:4,5)
  * [Issue 4](https://code.google.com/p/tamacat/issues/detail?id=4): Fault correction by which a scheme and authrity are contained in the path of reverse request URL.
  * tamacat-core-1.0.1 ([Issue 6](https://code.google.com/p/tamacat/issues/detail?id=6): DIContainer missing initialize bug)
  * Use HttpCore-4.2.3/HttpClient-4.2.3
  * We advise users of previous versions to upgrade.

## 1.0.2 Release (2012-11-18) ##

---

  * This is a maintenance release that fixes a number of bugs.
  * ClientIPAccessControlFilter support subnetmask.
  * Use HttpCore-4.2.2/HttpClient-4.2.2

## 1.0.1 Release (2012-10-23) ##

---

  * Bug fix When httpcore-4.2.2 is used, a reverseUrl infinite loop error occurs in ReverseProxyHandler. ([Issue 3](https://code.google.com/p/tamacat/issues/detail?id=3))
  * Bug fix virtual host. (Host header with port number)
  * Use HttpCore-4.2.2/HttpClient-4.2.1

## 1.0 Release (2012-06-14) ##

---

The highlights in the tamacat-httpd-1.0 are the following:

  * Many bug fix and refactoring.
  * Use HttpComponents 4.2 (HttpCore-4.2/HttpClient-4.2)

## 0.9 Release (2012-01-10) ##

---

The highlights in the tamacat-httpd-0.9 are the following:

  * Add load balancing method. (lb-method="LeastConnection" or "RoundRobin" in url-config.xml.)
  * Many bug fix and refactoring.
  * Use HttpComponents 4.1.x (HttpCore-4.1.4/HttpClient-4.1.2)

## 0.8 Release (2011-06-07) ##

---

The highlights in the tamacat-httpd-0.8 are the following:

  * Support session persistence.
  * Many bug fix and refactoring.
  * Use HttpComponents 4.1.1 (HttpCore-4.1.1/HttpClient-4.1.1)


## 0.7 release (2010-08-27) ##

---

The highlights in the tamacat-httpd-0.7 are the following:

  * Add the Form based Authentication. (FormAuthProcessor)
  * Modify interface methods (RequestFilter/ResponseFilter)
  * Use HttpComponents 4.1 (HttpCore-4.1-beta1/HttpClient-4.1-alpha2)


## 0.6 release (2010-03-16) ##

---

The highlights in the tamacat-httpd-0.6 are the following:

  * Virtual host configurations.
  * Client IP address access control.
  * Access and performance counter using JMX.
  * Refactoring for customizable access logging.
  * Add the response filter.
  * Implements session manager.(use from SessionCookieFilter)
  * URL based access counter.


## 0.5 release (2009-12-16) ##

---

The highlights in the tamacat-httpd-0.5 are the following:

  * Reloadable configuration file using JMX.
  * Add the JMX stop/start remote operations.
  * HTML link conversion on reverse proxy.
  * Gzip compression with content-type header.


## 0.4 release (2009-08-25) ##

---

The highlights in the tamacat-httpd-0.4 are the following:

  * Add directory listings page. (velocity template)
  * Load Balancer support.(Round Robin)
  * Changed to DI container (springframework -> tamacat-core)

## 0.3 release (2009-07-29) ##

---

The first development release of tamacat-httpd:

  * Implements http/https server with reverse proxy.
  * Customizable HTML page with velocity template.