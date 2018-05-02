Ansible Role: Commons Facts on OpenShift
[![Build Status](https://travis-ci.org/siamaksade/ansible-openshift-common-facts.svg?branch=master)](https://travis-ci.org/siamaksade/ansible-openshift-common-facts)
=========

Ansible Role for setting commons facts such as apps hostname and OpenShift CLI options.

This role sets the following facts to be used by other roles:

* `apps_hostname_suffix`: apps route hostname suffix

  An example of using this fact would be `app-project.{{ apps_hostname_suffix }}`

* `openshift_master`: openshift master

  If `oc` is already authenticated to OpenShift master, the same master url would be set as this fact

* `openshift_cli`: `oc` command appened with authentication and server options 

  Example: `oc --server=https://master:8443 --insecure-skip-tls-verify=true --token="lg4a*******"`


Role Variables
------------

| Variable             | Default Value | Description   |
|----------------------|---------------|---------------|
|`set_hostname_suffix` | true          | If true, the default apps hostname is set as the `apps_hostname_suffix` fact |
|`oc_kube_config`      | -             | Path to .kube/config to be used for authentication to OpenShift master |
|`oc_token`            | -             | OpenShift CLI token to be used for authentication to OpenShift master |
|`openshift_master`    | -             | OpenShift master |


Example Playbook
------------

```
name: Example Playbook
hosts: localhost
tasks:
- import_role:
    name: siamaksade.openshift_common_facts
  vars:
    set_hostname_suffix: false
    oc_token: "lg4a*******"
    openshift_master: "http://master:8443"
```