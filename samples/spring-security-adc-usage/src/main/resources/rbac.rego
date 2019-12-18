package rbac

import data.user2policy as u2p

default allow = false

allow {
    read
}

allow {
    admin
}

allow {
    readAllConfidentialData
}

# DEFINE POLICY read {
#     GRANT RULE read;
# }
read {
    u2p[input.user][_] == "read"
    input.actions.names[_] == "read"
}

# DEFINE POLICY adminConfidential {
#     GRANT RULE admin;
#     GRANT POLICY readAllConfidentialData;
# }
admin {
    u2p[input.user][_] == "admin"
    actions := ["read", "write"]
    input.actions.names[_] == actions[_]
    readAllConfidentialData
}

# DEFINE POLICY readAllConfidentialData {
#    GRANT RULE read WHERE confidentiality = "CONFIDENTIAL" OR confidentiality = "STRICTLY_CONFIDENTIAL";
# }
readAllConfidentialData {
    u2p[input.user][_] == "readAllConfidentialData"
    input.actions.names[_] == "read"
#    some i
#    values := ["CONFIDENTIAL", "STRICTLY_CONFIDENTIAL"]
#    input.attributes[i].name == "confidentiality"
#    input.attributes[i].value == values[_]

     input.attributes[_].name == "confidentiality"
     input.attributes[_].value == "CONFIDENTIAL"
}