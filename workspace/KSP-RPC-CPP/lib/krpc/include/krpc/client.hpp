#pragma once

#include <google/protobuf/stubs/port.h>

#include <functional>
#include <map>
#include <memory>
#include <mutex>  // NOLINT(build/c++11)
#include <string>
#include <utility>
#include <vector>

#include "krpc/krpc.pb.hpp"

namespace krpc {

class Connection;
class StreamManager;
class StreamImpl;

class Client {
 public:
  Client();
  Client(const std::string& name, const std::string& address,
         unsigned int rpc_port = 50000, unsigned int stream_port = 50001);

  std::string invoke(const schema::Request& request);
  std::string invoke(const schema::ProcedureCall& call);
  std::string invoke(
    const std::string& service, const std::string& procedure,
    const std::vector<std::string>& args = std::vector<std::string>());

  schema::Request build_request(
    const std::string& service, const std::string& procedure,
    const std::vector<std::string>& args = std::vector<std::string>());
  schema::ProcedureCall build_call(
    const std::string& service, const std::string& procedure,
    const std::vector<std::string>& args = std::vector<std::string>());
  void add_exception_thrower(const std::string& service, const std::string& name,
                             const std::function<void(std::string)>& thrower);

 private:
  friend class StreamManager;
  void throw_exception(const schema::Error& error) const;

 public:
  std::shared_ptr<StreamImpl> add_stream(const schema::ProcedureCall& call);
  std::shared_ptr<StreamImpl> get_stream(google::protobuf::uint64 id);
  void remove_stream(google::protobuf::uint64 id);
  void freeze_streams();
  void thaw_streams();

 private:
  std::shared_ptr<Connection> rpc_connection;
  std::shared_ptr<StreamManager> stream_manager;
  std::shared_ptr<std::mutex> lock;
  std::map<std::pair<std::string, std::string>,
           std::function<void(std::string)>> exception_throwers;
};

}  // namespace krpc
