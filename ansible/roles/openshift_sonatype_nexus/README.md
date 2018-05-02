Ansible Role: Sonatype Nexus on OpenShift
[![Build Status](https://travis-ci.org/siamaksade/ansible-openshift-nexus.svg?branch=master)](https://travis-ci.org/siamaksade/ansible-openshift-nexus)
=========

Ansible Role for deploying Sonatype Nexus on OpenShift with Red Hat enterprise maven repositories preconfigure on Nexus


Role Variables
------------

|Variable               | Default Value            | Description   |
|-----------------------|--------------------------|---------------|
|`nexus_image_version`  | 3.7.1                    | Nexus image version as available on Docker Hub for [Nexus 2](https://hub.docker.com/r/sonatype/nexus/tags/) and [Nexus 3](https://hub.docker.com/r/sonatype/nexus3/tags) |
|`nexus_volume_capacity`| 10Gi                     | Persistent volume capacity for Nexus  |
|`nexus_max_memory`     | 2Gi                      | Memory allocated to Nexus container |
|`project_name`         | nexus                    | OpenShift project name for the Nexus container  |
|`project_display_name` | Nexus                    | OpenShift project display name for the Nexus container  |
|`project_desc`         | Nexus Repository Manager | OpenShift project description for the Nexus container |
|`project_annotations`  | -                        | OpenShift project annotations for the Nexus container |
|`openshift_cli`        | oc                       | OpenShift CLI command and arguments (e.g. auth)       | 


Example Playbook
------------

```
name: Example Playbook
hosts: localhost
tasks:
- import_role:
    name: siamaksade.openshift_nexus
  vars:
    project_name: "cicd-project"
    nexus_image_version: "3.7.2"
    openshift_cli: "oc --server http://master:8443"
```