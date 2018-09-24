package com.yoozoo.protoconf;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: agentApplicationService.proto")
public final class AgentApplicationServiceGrpc {

  private AgentApplicationServiceGrpc() {}

  public static final String SERVICE_NAME = "agentApplicationService.AgentApplicationService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest,
      com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse> METHOD_GET_LOGON_INFO =
      io.grpc.MethodDescriptor.<com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest, com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "agentApplicationService.AgentApplicationService", "getLogonInfo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AgentApplicationServiceStub newStub(io.grpc.Channel channel) {
    return new AgentApplicationServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AgentApplicationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AgentApplicationServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AgentApplicationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AgentApplicationServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class AgentApplicationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getLogonInfo(com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest request,
        io.grpc.stub.StreamObserver<com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_LOGON_INFO, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_LOGON_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest,
                com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse>(
                  this, METHODID_GET_LOGON_INFO)))
          .build();
    }
  }

  /**
   */
  public static final class AgentApplicationServiceStub extends io.grpc.stub.AbstractStub<AgentApplicationServiceStub> {
    private AgentApplicationServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgentApplicationServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgentApplicationServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgentApplicationServiceStub(channel, callOptions);
    }

    /**
     */
    public void getLogonInfo(com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest request,
        io.grpc.stub.StreamObserver<com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_LOGON_INFO, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AgentApplicationServiceBlockingStub extends io.grpc.stub.AbstractStub<AgentApplicationServiceBlockingStub> {
    private AgentApplicationServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgentApplicationServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgentApplicationServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgentApplicationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse getLogonInfo(com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_LOGON_INFO, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AgentApplicationServiceFutureStub extends io.grpc.stub.AbstractStub<AgentApplicationServiceFutureStub> {
    private AgentApplicationServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgentApplicationServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgentApplicationServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgentApplicationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse> getLogonInfo(
        com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_LOGON_INFO, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_LOGON_INFO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AgentApplicationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AgentApplicationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_LOGON_INFO:
          serviceImpl.getLogonInfo((com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.yoozoo.protoconf.AgentApplicationServiceOuterClass.LogonInfoResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class AgentApplicationServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.yoozoo.protoconf.AgentApplicationServiceOuterClass.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AgentApplicationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AgentApplicationServiceDescriptorSupplier())
              .addMethod(METHOD_GET_LOGON_INFO)
              .build();
        }
      }
    }
    return result;
  }
}
