# Complex Network Services using OpenFlow Barebones

João Leite

#### Keywords

SDN, NFV, network services, OpenFlow, NAT


#### Abstract

Networks are increasingly complex. Lately, several new paradigms have been proposed with the goal of decoupling the network management from the physical infrastructure, either by segregating the control and data planes (SDN) or by virtualizing network functions (NFV). In spite of the potential of these paradigms, the complexity of SDN controllers programming has led to the increased usage of NFV to implement common network services. NFV can introduce overhead in some network service implementations because all traffic has to be redirected to the VNF virtual machine. The NBI normalization in SDN is a possible solution to this problem by introducing a common and simpler API for the implementation of network services using a SDN-only solution. On the other hand, the SBI is already normalized by the OpenFlow protocol, however, it is challenging to implement network services using only OpenFlow rules and there are few implementations available. 

In this thesis, the advantages of a SDN-only solution for the implementation of common network services in contrast to a NFV solution will be discussed. Network services like a NAT or a Layer 3 Routing will be implemented using OpenFlow rules. These implementations will be discussed and evaluated comparing to NFV and traditional approaches.

#### Folder structure

* docker: Dockerfiles and docker-compose
* floodlight: Git sub-tree of the Floodlight SDN Controller (including contributions)
* mininet: Network emulator configuration and results





