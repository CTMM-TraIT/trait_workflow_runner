@rem ******
@rem * Copyright 2014 VU University Medical Center.
@rem * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
@rem *
@rem *
@rem * This batch file can be used to run a number of checks (on Windows):
@rem * unit tests, code coverage, Checkstyle, FindBugs, PMD, and CPD.
@rem *
@rem * author: Freek de Bruijn (f.debruijn@vumc.nl)
@rem ******


@echo Running all checks for Workflow Runner...

@echo.
@echo ================================================================================================================
@echo Clean the project (mvn clean)
@call mvn clean

@echo.
@echo ================================================================================================================
@echo Run all unit tests (mvn test) and generate code coverage report (in target\site\code-coverage-jacoco)
@pause
@call mvn test

@echo.
@echo ================================================================================================================
@echo Check for Checkstyle issues (mvn checkstyle:checkstyle - report in target\checkstyle-result.xml)
@pause
@call mvn checkstyle:checkstyle
@if not "%ERRORLEVEL%" == "0" less target/checkstyle-result.xml

@echo.
@echo ================================================================================================================
@echo Check for FindBugs issues (mvn site - report in target\findbugsXml.xml)
@pause
@call mvn site
@if not "%ERRORLEVEL%" == "0" less target/findbugsXml.xml

@echo.
@echo ================================================================================================================
@echo Check for PMD issues (mvn compile pmd:check - report in target\pmd.xml)
@pause
@call mvn compile pmd:check
@if not "%ERRORLEVEL%" == "0" less target/pmd.xml

@echo.
@echo ================================================================================================================
@echo Check for CPD issues (mvn compile pmd:cpd-check - report in target\cpd.xml)
@pause
@call mvn compile pmd:cpd-check
@if not "%ERRORLEVEL%" == "0" less target/cpd.xml
