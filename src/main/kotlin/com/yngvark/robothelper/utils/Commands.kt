package com.yngvark.robothelper.utils

const val SLACK_COMMAND = "/usr/bin/flatpak run --branch=stable --arch=x86_64 --command=slack --file-forwarding com.slack.Slack --disable-gpu @@u %U @@"
