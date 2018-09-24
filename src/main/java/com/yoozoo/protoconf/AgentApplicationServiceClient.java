package com.yoozoo.protoconf;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AgentApplicationServiceClient {
    private String appToken;
    private String env;

    public AgentApplicationServiceClient(String appToken, String env) {
        this.appToken = appToken;
        this.env = env;
    }

    public AgentApplicationServiceOuterClass.LogonInfoResponse getEtcdConfig() throws Exception {
        // Channel is the abstraction to connect to a service endpoint
        // Let's use plaintext communication because we don't have certs
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 57581)
                .usePlaintext(true)
                .build();

        // It is up to the client to determine whether to block the call
        // Here we create a blocking stub, but an async stub,
        // or an async stub with Future are always possible.
        AgentApplicationServiceGrpc.AgentApplicationServiceBlockingStub stub =
                AgentApplicationServiceGrpc.newBlockingStub(channel);
        AgentApplicationServiceOuterClass.LogonInfoRequest req =
                AgentApplicationServiceOuterClass.LogonInfoRequest.newBuilder()
                        .setAppToken(appToken)
                        .setEnv(env)
                        .build();

        // Finally, make the call using the stub
        AgentApplicationServiceOuterClass.LogonInfoResponse resp =
                stub.getLogonInfo(req);

        System.out.println(resp);

        // A Channel should be shutdown before stopping the process.
        channel.shutdownNow();

        return resp;
    }
}
