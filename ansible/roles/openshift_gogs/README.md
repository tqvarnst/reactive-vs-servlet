Ansible Role: Gogs Git Server on OpenShift
[![Build Status](https://travis-ci.org/siamaksade/ansible-openshift-gogs.svg?branch=master)](https://travis-ci.org/siamaksade/ansible-openshift-gogs)
=========

Ansible Role for deploying Gogs Git Server on OpenShift. This role creates an admin 
account, a user account and also if configured would generate the specified number of user 
accounts for Gogs.


Role Variables
------------

| Variable                  | Default Value   | Description   |
|---------------------------|-----------------|---------------|
|`gogs_image_version`       | 0.11.29         | Gogs image version as available on [Docker Hub](https://hub.docker.com/r/openshiftdemos/gogs/tags/) |
|`gogs_route`               | gogs-{{ project_name }}.127.0.0.1.nip.io | **Required**. Gogs hostname to be configure |
|`gogs_admin_user`          | gogs            | Admin account username |
|`gogs_admin_password`      | gogs            | Admin account password |
|`gogs_user`                | developer       | User account username |
|`gogs_password`            | developer       | User account password |
|`gogs_generate_user_count` | 0               | Number of users accounts to generate with the user account password |
|`gogs_generate_user_format`| user%02d        | [printf style format](https://en.wikipedia.org/wiki/Printf_format_string) to use for generating user accounts |
|`clean_deploy`             | false           | Deploy a fresh Gogs and delete the existing one if any |
|`project_name`             | gogs            | OpenShift project name for the Gogs container  |
|`project_display_name`     | Gogs            | OpenShift project display name for the Gogs container  |
|`project_desc`             | Gogs Git Server | OpenShift project description for the Gogs container |
|`project_admin`            | -               | If set, the user to be assigned as project admin |
|`project_annotations`      | -               | OpenShift project annotations for the Gogs container |
|`openshift_cli`            | oc              | OpenShift CLI command and arguments (e.g. auth)       | 


Example Playbook
------------

```
name: Example Playbook
hosts: localhost
tasks:
- import_role:
    name: siamaksade.openshift_gogs
  vars:
    gogs_route: "gogs-cicd-project.apps.myopenshift.com"
    project_name: "cicd-project"
    gogs_generate_user_count: "50"
    openshift_cli: "oc --server http://master:8443"
```