#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <netlink/netlink.h>
#include <netlink/genl/genl.h>
#include <linux/taskstats.h>
#include <jansson.h>
#include <syslog.h>


#define MAX_PAYLOAD 1024

static int netlink_recv_cb(struct nl_msg *msg, void *arg) {
    struct nlmsghdr *nh;
    struct genlmsghdr *gh;
    struct taskstats *ts;
    struct nlattr *attrs[TASKSTATS_TYPE_MAX+1];
    int len;
    char *buf;
    json_t *root;
    json_t *pid;
    char *json_str;

    nh = nlmsg_hdr(msg);
    gh = (struct genlmsghdr *)nlmsg_data(nh);
    ts = (struct taskstats *)genlmsg_data(gh);

    if (nla_parse(attrs, TASKSTATS_TYPE_MAX, genlmsg_attrdata(gh, 0),
                  genlmsg_attrlen(gh, 0), NULL) < 0) {
        syslog(LOG_ERR, "nla_parse failed");
        return NL_SKIP;
    }

    if (!attrs[TASKSTATS_CMD_ATTR_PID]) {
        syslog(LOG_ERR, "no pid attribute");
        return NL_SKIP;
    }

    root = json_object();
    pid = json_integer(nla_get_u32(attrs[TASKSTATS_CMD_ATTR_PID]));
    json_object_set(root, "pid", pid);
    json_str = json_dumps(root, 0);
    syslog(LOG_INFO, "%s", json_str);
    free(json_str);
    json_decref(pid);
    json_decref(root);

    return NL_SKIP;
}

int main(int argc, char *argv[]) {
    // ...

    while (1) {
        nl_recvmsgs_default(sock);
        nl_clear_errormsg();
    }

    // ...

    return 0;
}


/*  这里的作用不一定对
input {
    netlink {
        type => "process"
        family => "TASKSTATS"
    }
}

output {
    elasticsearch {
        hosts => ["localhost:9200"]
        index => "process_events"
    }
}

*/
