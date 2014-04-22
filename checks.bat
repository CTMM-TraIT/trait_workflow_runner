@rem ******
@rem * Copyright 2014 VU University Medical Center.
@rem * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
@rem *
@rem *
@rem * This batch file can be used to run a number of checks (on Windows):
@rem * unit tests, code coverage, Checkstyle, FindBugs, PMD, and CPD.
@rem *
@rem * author: Freek de Bruijn (f.debruijn@vumc.nl)
@rem ******


@echo Running all checks for Workflow Runner...

@echo.
@echo ============================
@echo Clean the project
@call mvn clean

@echo.
@echo ============================
@echo Run all unit tests and generate code coverage report (in target\site\code-coverage-jacoco)
@pause
@call mvn test

@echo.
@echo ============================
@echo Check for Checkstyle issues (report in target\checkstyle-result.xml)
@pause
@call mvn checkstyle:checkstyle
@if not "%ERRORLEVEL%" == "0" less target/checkstyle-result.xml

@echo.
@echo ============================
@echo Check for FindBugs issues (report in target\findbugsXml.xml)
@pause
@call mvn compile findbugs:check
@if not "%ERRORLEVEL%" == "0" less target/findbugsXml.xml

@echo.
@echo ============================
@echo Check for PMD issues (report in target\pmd.xml)
@pause
@call mvn compile pmd:check
@if not "%ERRORLEVEL%" == "0" less target/pmd.xml

@echo.
@echo ============================
@echo Check for CPD issues (report in target\cpd.xml)
@pause
@call mvn compile pmd:cpd-check
@if not "%ERRORLEVEL%" == "0" less target/cpd.xml