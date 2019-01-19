#pragma once

#include <google/protobuf/stubs/port.h>

#include <atomic>
#include <map>
#include <memory>
#include <mutex>  // NOLINT(build/c++11)
#include <thread>  // NOLINT(build/c++11)

namespace krpc {

class Client;
class Connection;
class StreamImpl;
namespace schema {
class ProcedureCall;
class ProcedureResult;
}

class StreamManager {
 public:
  StreamManager(Client* client, const std::shared_ptr<Connection>& connection);
  ~StreamManager();
  std::shared_ptr<StreamImpl> add_stream(const schema::ProcedureCall& call);
  std::shared_ptr<StreamImpl> get_stream(google::protobuf::uint64 id);
  void remove_stream(google::protobuf::uint64 id);
  void update(google::protobuf::uint64 id, const schema::ProcedureResult& result);
  void freeze();
  void thaw();

 private:
  static void update_thread_main(StreamManager* stream_manager,
                                 const std::shared_ptr<Connection>& connection,
                                 const std::shared_ptr<std::atomic_bool>& stop,
                                 const std::shared_ptr<std::atomic_bool>& should_freeze,
                                 const std::shared_ptr<std::atomic_bool>& frozen);
  Client* client;
  std::shared_ptr<Connection> connection;
  std::map<google::protobuf::uint64, std::weak_ptr<StreamImpl>> streams;
  std::shared_ptr<std::recursive_mutex> update_lock;
  std::shared_ptr<std::atomic_bool> stop;
  std::shared_ptr<std::atomic_bool> should_freeze;
  std::shared_ptr<std::atomic_bool> frozen;
  std::shared_ptr<std::thread> update_thread;
};

}  // namespace krpc
