# Protobuf

Protocol buffers, SLL Socket

 1.Authorization via POST request.

2. Get Items list from Checklist account

This part of protocol sends Binary Protobufs over raw SSL channel. Each block of data (both inbound and outbound) consists of 64 bit sequence number, header and serialized Protobuf Request structure. Sequence number for the first request is 0, than it is returned by server and should be used for subsequent requests. Header is 64 bit Little-Endian integer, upper 32 bits are reserved for flags (zeroed), lower 32 bits represent the size of the upcoming data.

3. Showing Checklist data
