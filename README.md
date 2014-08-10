# Workflow Runner

[![Build Status](https://travis-ci.org/CTMM-TraIT/trait_workflow_runner.png)](https://travis-ci.org/CTMM-TraIT/trait_workflow_runner)
[![Coverage Status](https://coveralls.io/repos/CTMM-TraIT/trait_workflow_runner/badge.png)](https://coveralls.io/r/CTMM-TraIT/trait_workflow_runner)

This library is part of a larger project to connect [tranSMART](http://transmartfoundation.org/) to [Galaxy](https://usegalaxy.org/), which is made possible by the [BioMedBridges](http://www.biomedbridges.eu/) and [CTMM TraIT](http://www.ctmm-trait.nl/) projects. tranSMART is a scientific data management system and Galaxy is a web-based platform for data intensive biomedical research.

The goal of this library is to make it easy for tranSMART users to run a workflow in Galaxy. The Workflow Runner library is written in Java and uses the [blend4j](https://github.com/jmchilton/blend4j) library for accessing Galaxy servers via the [Galaxy API](https://wiki.galaxyproject.org/Events/GCC2013/TrainingDay/API).

We strive to create a group of components that will both be needed for building a specific bridge between tranSMART and Galaxy, and be useful for making bridges between other systems (like for example tranSMART-[Molgenis](http://www.molgenis.org/wiki/WikiStart) or Molgenis-Galaxy).

For developers, we have a separate page with information on the development tools we use (git, GitHub, Maven, Checkstyle, FindBugs, PMD and CPD): [DevelopersInformation.md](DevelopersInformation.md).
