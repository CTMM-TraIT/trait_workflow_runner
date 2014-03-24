# Workflow Runner


This tool is part of a larger project to connect [tranSMART](http://transmartfoundation.org/) to [Galaxy](https://usegalaxy.org/), which is made possible by the [BioMedBridges](http://www.biomedbridges.eu/) and [CTMM TraIT](http://www.ctmm-trait.nl/) projects. tranSMART is a scientific data management system and Galaxy is a web-based platform for data intensive biomedical research.

The goal of this tool is to make it easy to run a workflow from tranSMART in Galaxy using the [Galaxy API](https://wiki.galaxyproject.org/Events/GCC2013/TrainingDay/API). The Workflow Runner tool is written in Java and uses the [blend4j](https://github.com/jmchilton/blend4j) library for accessing Galaxy servers using the Galaxy API.

We strive to create a group of components that will both be needed for building a specific bridge between tranSMART and Galaxy, and be useful for making bridges between other systems (like for example tranSMART-Molgenis or Molgenis-Galaxy).

For developers we have a separate page with information on the tools we use (git, GitHub, Maven, Checkstyle, FindBugs, PMD and CPD): [DevelopersInformation.md](DevelopersInformation.md).
