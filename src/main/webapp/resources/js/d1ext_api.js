;(function () {

   var presentPromise = !!window.Promise;
   var d1object;

   function d1class()
   {
      this.version = "1.1.0.9";

      this.activex = {};
      this.npapi = {};
      this.webui = false;

      this.timeout = 120000;
      this.src = "";
      this.reqid = 0;
      this.resolve_arr = {};
      this.reject_arr = {};
      this.timeouts = {};
      this.SessionID = 0;
      this.RequestID = 0;
      this.StoreID = 0;
      this.Cert = "";
      this.OCSP = "";
      this.TSP = "";
      this.ProvType = 0;
      this.HashOID = "";

      this.Init = function()
      {
         this.src = this.GenSrc();
         window.addEventListener("message", this.Message, false);
      };
    
      this.Message = function(event) 
      {
         if (event.source != window || event.data.dst != d1object.src || (event.data.src != "d1ext" && event.data.src != "d1nmh"))
         {
            return;
         };
         if (event.data.type != "error" && typeof(d1object.resolve_arr[event.data.reqid]) != "undefined")
         {  
            d1object.resolve_arr[event.data.reqid](d1object.ProcessResponse(event.data));
         } else if (typeof(d1object.reject_arr[event.data.reqid]) != "undefined")
         {
            d1object.reject_arr[event.data.reqid](event.data.data);
         };
         clearTimeout(d1object.timeouts[event.data.reqid]);
      };

      this.Call = function()
      {
         var args = arguments;
         var rid = ++ this.reqid;
         var tm = this.timeout;

         return new Promise(
            function(resolve, reject)
            {
               d1object.resolve_arr[rid] = resolve;
	       d1object.reject_arr[rid] = reject;
               d1object.CallMethod(args);
               d1object.timeouts[rid] = setTimeout(function()
                 {
                    d1object.reject_arr[rid]({code: -1, mess: "Operation timeout"});
                 }, args[1]);
            }
         )
      };

      // [0]: "get_methods", "method", "check_*"
      // [1]: timeout
      // [2]: method name
      // [3]..[n]: parameters
      this.CallMethod = function(arguments_)
      {
         args = new Array();
         var arg;
         for(var i = 3; i < arguments_.length; i++)
         {
            arg = {type: typeof arguments_[i], value: arguments_[i]};
            args.push(arg);
         }

         dst_path = "";
         if (!this.webui)
            dst_path = "d1ext";
         else
            dst_path = "d1nmh";

         object_messsage = {src: this.src, dst: dst_path, reqid: this.reqid, type: arguments_[0]};

         if (arguments_[0] == "method")
         {
            object_messsage.data = {};
            object_messsage.data.name = arguments_[2];
            object_messsage.data.params = args;
         };

         if (!this.webui)
            window.postMessage(object_messsage, "*");
         else
            chrome.send('d1nmh', [JSON.stringify(object_messsage)]);
      };

      this.GenSrc = function()
      {
         function rp()
         {
            return Math.floor(Math.random() * 0x100000000).toString(16).toUpperCase();
         };
         return rp() + "-" + rp();
      };

      this.Init();
      this.CheckContent = function(timeout)
      {
         return this.Call("check_content", timeout);
      };

      this.CheckBackground = function(timeout)
      {
         return this.Call("check_background", timeout);
      };

      this.CheckNMH = function(timeout)
      {
         return this.Call("check_nmh", timeout);
      };

      this.Check = function(deep, timeout, fn_resolve, fn_reject)
      {
        if (!this.webui)
        {
          var self = this;
          return this.CheckContent(timeout).then(
             function(ret)
             {
                if (deep)
                    deep--;
                if (deep)
                   return self.CheckBackground(timeout);
                else
                   return ret;
             },
             function(ret)
             {
                throw ret;
             }
          ).then(
             function(ret)
             {
                if (deep)
                   deep--;
                if (deep)
                   return self.CheckNMH(timeout);
                else
                   return ret;
             },
             function(ret)
             {
                throw ret;
             }
          ).then(
             function(ret)
             {
                return fn_resolve(ret);
             },
             function(ret)
             {
                return fn_reject(ret);
             }
          );
        } else
        {
           return this.CheckNMH(timeout).then(fn_resolve, fn_reject);
        };
      };

      this.ProcessResponse = function(response)
      {
          if (response.type == "result")
          {
             if (response.data.name == "CreateSession") {
                return this.CreateSessionResponse(response.data);
             } else if (response.data.name == "CloseSession") {
                return this.CloseSessionResponse(response.data);
             } else if (response.data.name == "CreateHashSession") {
                return this.CreateHashSessionResponse(response.data)
             } else if (response.data.name == "ReqCreateFromBuffer") {
                return this.ReqCreateFromBufferResponse(response.data)
             } else if (response.data.name == "ReqCreateFromFile") {
                return this.ReqCreateFromFileResponse(response.data)
             } else if (response.data.name == "ReqRelease") {
                return this.ReqReleaseResponse(response.data)
             } else if (response.data.name == "StoreCreate") {
                return this.StoreCreateResponse(response.data)
             } else if (response.data.name == "StoreClose") {
                return this.StoreCloseResponse(response.data)
             };

             return this.Response(response.data);

          } else if (response.type == "methods" || (response.type == "check"));
             return response.data;

          return {type: "error", data: {code: 0, mess: "D1Ext: Unknown type"}};
      };

      // Методы Native Messagin Host
      // GetMethods
      //this.GetMethods = this.Call.bind(this.Call, "get_methods");
      this.GetMethods = function()
      {
         return this.Call("get_methods", this.timeout);
      };

      // Общий Response
      this.Response = function(resp_data)
      {
         return resp_data.value;
      };

      // CreateSession
      this.CreateSession = function(CertID, Flags)
      {
         return this.Call("method", this.timeout, "CreateSession", CertID, this.OCSP, this.TSP, this.ProvType, this.HashOID, Flags);
      };
      this.CreateSessionResponse = function(resp_data)
      {
         this.SessionID = resp_data.value;
         return resp_data.value;
      };

      // CloseSession
      this.CloseSession = function()
      {
         return this.Call("method", this.timeout, "CloseSession", this.SessionID);
      };
      this.CloseSessionResponse = function(resp_data)
      {
         this.SessionID = 0;
         return resp_data.value;
      };

      // CreateMessageFromBuffer
      this.CreateMessageFromBuffer = function(Msg, Flags)
      {
         return this.Call("method", this.timeout, "CreateMessageFromBuffer", Msg, Flags);
      };

      // OpenMessageFromBuffer
      this.OpenMessageFromBuffer = function(Msg, Flags)
      {
         return this.Call("method", this.timeout, "OpenMessageFromBuffer", Msg, Flags);
      };

      // SignMessage
      this.SignMessage = function(MsgID, Flags)
      {
         return this.Call("method", this.timeout, "SignMessage", this.SessionID, MsgID, Flags);
      };

      // GetMessageBody
      this.GetMessageBody = function(MsgID, Flags)
      {
         return this.Call("method", this.timeout, "GetMessageBody", MsgID, Flags);
      };

      // GetMessageContent
      this.GetMessageContent = function(MsgID, Flags)
      {
         return this.Call("method", this.timeout, "GetMessageContent", MsgID, Flags);
      };

      // ReleaseMessageBody
      this.ReleaseMessage = function(MsgID)
      {
         return this.Call("method", this.timeout, "ReleaseMessage", MsgID);
      };

      // VerifyMessageBody
      this.VerifyMessage = function(MsgID, SignIndex, Flags)
      {
         return this.Call("method", this.timeout, "VerifyMessage", this.SessionID, MsgID, SignIndex, Flags);
      };

      // GetMessageInfos
      this.GetMessageInfos = function(MsgID, SignIndex, Flags)
      {
         return this.Call("method", this.timeout, "GetMessageInfos", MsgID, SignIndex, Flags);
      };

      // GetMessageInfo
      this.GetMessageInfo = function(MsgID, SignIndex, ParamID, ParamOID, Flags)
      {
         return this.Call("method", this.timeout, "GetMessageInfo", MsgID, SignIndex, ParamID, ParamOID, Flags);
      };

      // GetSessionInfo
      this.GetSessionInfo = function(ID, OID, Flags)
      {
         return this.Call("method", this.timeout, "GetSessionInfo", this.SessionID, ID, OID, Flags);
      };

      // CreateMessageFromFile
      this.CreateMessageFromFile = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "CreateMessageFromFile", FileName, Flags);
      };

      // OpenMessageFromFile
      this.OpenMessageFromFile = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "OpenMessageFromFile", FileName, Flags);
      };

      // SetMessageContentFromBuffer
      this.SetMessageContentFromBuffer = function(MsgID, Msg, Flags)
      {
         return this.Call("method", this.timeout, "SetMessageContentFromBuffer", MsgID, Msg, Flags);
      };

      // SetMessageContentFromFile
      this.SetMessageContentFromFile = function(MsgID, FileName, Flags)
      {
         return this.Call("method", this.timeout, "SetMessageContentFromFile", MsgID, FileName, Flags);
      };

      // SaveMessageBody
      this.SaveMessageBody = function(MsgID, FileName, Flags)
      {
         return this.Call("method", this.timeout, "SaveMessageBody", MsgID, FileName, Flags);
      };

      // SaveMessageContent
      this.SaveMessageContent = function(MsgID, FileName, Flags)
      {
         return this.Call("method", this.timeout, "SaveMessageContent", MsgID, FileName, Flags);
      };

      // DeleteMessageSign
      this.DeleteMessageSign = function(MsgID, SignIndex, Flags)
      {
         return this.Call("method", this.timeout, "DeleteMessageSign", MsgID, SignIndex, Flags);
      };

      // HashMessage
      this.HashMessage = function(MsgID, Flags)
      {
         return this.Call("method", this.timeout, "HashMessage", this.SessionID, MsgID, Flags);
      };

      // CreateHashSession
      this.CreateHashSession = function(CertID, ProvType, HashOID, Flags)
      {
         return this.Call("method", this.timeout, "CreateHashSession", CertID, ProvType, HashOID, Flags);
      };
      this.CreateHashSessionResponse = function(resp_data)
      {
         this.SessionID = resp_data.value;
         return resp_data.value;
      };

      // Log
      this.Log = function(On, FileName)
      {
         return this.Call("method", this.timeout, "Log", On, FileName);
      };

      // Version
      this.Version = function()
      {
         return this.Call("method", this.timeout, "Version");
      };

      // StampMessage(long SessionID, long MessageID, long SignIndex, DWORD Flags);
      this.StampMessage = function(MessageID, SignIndex, Flags)
      {
         return this.Call("method", this.timeout, "StampMessage", this.SessionID, MessageID, SignIndex, Flags);
      };

      // Методы CSR

      // ReqCreateFromBuffer(BYTE* Buffer, DWORD BufferSize, DWORD Flags);
      this.ReqCreateFromBuffer = function(Buffer, Flags)
      {
         return this.Call("method", this.timeout, "ReqCreateFromBuffer", Buffer, Flags);
      };
      this.ReqCreateFromBufferResponse = function(resp_data)
      {
         this.RequestID = resp_data.value;
         return resp_data.value;
      };

      // ReqCreateFromFile(char* FileName, DWORD Flags);
      this.ReqCreateFromFile = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "ReqCreateFromFile", FileName, Flags);
      };
      this.ReqCreateFromFileResponse = function(resp_data)
      {
         this.RequestID = resp_data.value;
         return resp_data.value;
      };

      // ReqRelease(long RequestID);
      this.ReqRelease = function()
      {
         return this.Call("method", this.timeout, "ReqRelease", this.RequestID);
      };
      this.ReqReleaseResponse = function(resp_data)
      {
         this.RequestID = 0;
         return resp_data.value;
      };

      // ReqAddRDN(long RequestID, char* OID, BYTE* Buffer, DWORD BufferSize, DWORD AttrType, DWORD Flags);
      this.ReqAddRDN = function(OID, Buffer, AttrType, Flags)
      {
         return this.Call("method", this.timeout, "ReqAddRDN", this.RequestID, OID, Buffer, AttrType, Flags);
      };

      // ReqAddRDNS(long RequestID, Array(char* OID, BYTE* Buffer, DWORD BufferSize, DWORD AttrType), DWORD Flags);
      this.ReqAddRDNs = function(RDNs, Flags)
      {
         return this.Call("method", this.timeout, "ReqAddRDNs", this.RequestID, RDNs, Flags);
      };

      // ReqDelRDN(long RequestID, char* OID);
      this.ReqDelRDN = function(OID)
      {
         return this.Call("method", this.timeout, "ReqDelRDN", this.RequestID, OID);
      };

      // ReqClearRDN(long RequestID);
      this.ReqClearRDN = function()
      {
         return this.Call("method", this.timeout, "ReqClearRDN", this.RequestID);
      };

      // ReqSetPeriod(long RequestID, char* OID, char* DTStart, char* DTEnd, DWORD Flags);
      this.ReqSetPeriod = function(OID, DTStart, DTEnd, Flags)
      {
         return this.Call("method", this.timeout, "ReqSetPeriod", this.RequestID, OID, DTStart, DTEnd, Flags);
      };

      // ReqClearPeriod(long RequestID);
      this.ReqClearPeriod = function()
      {
         return this.Call("method", this.timeout, "ReqClearPeriod", this.RequestID);
      };

      // ReqAddExtension(long RequestID, char* OID, BYTE* Buffer, DWORD BufferSize, long Critical, DWORD Flags);
      this.ReqAddExtension = function(OID, Buffer, Critical, Flags)
      {
         return this.Call("method", this.timeout, "ReqAddExtension", this.RequestID, OID, Buffer, Critical, Flags);
      };

      // ReqDelExtension(long RequestID, char* OID);
      this.ReqDelExtension = function(OID)
      {
         return this.Call("method", this.timeout, "ReqDelExtension", this.RequestID, OID);
      };

      // ReqAddExtensions(long RequestID, Array(char* OID, BYTE* Buffer, DWORD BufferSize, DWORD Critical), DWORD Flags);
      this.ReqAddExtensions = function(extensions, Flags)
      {
         return this.Call("method", this.timeout, "ReqAddExtensions", this.RequestID, extensions, Flags);
      };

      // ReqClearExtension(long RequestID);
      this.ReqClearExtension = function()
      {
         return this.Call("method", this.timeout, "ReqClearExtension", this.RequestID);
      };

      // ReqBuild(long RequestID, char* ContainerName, DWORD ProvType, char* SignOID, DWORD Flags);
      this.ReqBuild = function(ContainerName, ProvType, SignOID, Flags)
      {
         return this.Call("method", this.timeout, "ReqBuild", this.RequestID, ContainerName, ProvType, SignOID, Flags);
      };

      // ReqBuildCard(long RequestID, BYTE* Buffer, DWORD BufferSize, DWORD Flags);
      this.ReqBuildCard = function(Buffer, Flags)
      {
         return this.Call("method", this.timeout, "ReqBuildCard", this.RequestID, Buffer, Flags);
      };

      // ReqGetBody(long RequestID, BYTE* Buffer, DWORD* BufferSize, DWORD Flags);
      this.ReqGetBody = function(Flags)
      {
         return this.Call("method", this.timeout, "ReqGetBody", this.RequestID, Flags);
      };

      // ReqSaveBody(long RequestID, char* FileName, DWORD Flags);
      this.ReqSaveBody = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "ReqSaveBody", this.RequestID, FileName, Flags);
      };

      // ReqGetPubKeyCard(long RequestID, BYTE* Buffer, DWORD* BufferSize, DWORD Flags);
      this.ReqGetPubKeyCard = function(Flags)
      {
         return this.Call("method", this.timeout, "ReqGetPubKeyCard", this.RequestID, Flags);
      };

      // ReqSavePubKeyCard(long RequestID, char* FileName, DWORD Flags);
      this.ReqSavePubKeyCard = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "ReqSavePubKeyCard", this.RequestID, FileName, Flags);
      };

      // ReqCreate(long RequestID, ...);
      this.ReqCreate = function(RDNS, RDNFlags, Exts, DTOID, DTStart, DTEnd, DTFlags, ContName, ProvType, SignOID, BuildFlags, BodyFlags)
      {
         return this.Call("method", this.timeout, "ReqCreate", this.RequestID, RDNS, RDNFlags, Exts, DTOID, DTStart, DTEnd, DTFlags, ContName, ProvType, SignOID, BuildFlags, BodyFlags);
      };

      // Cert
      // CertOpenFromBuffer(BYTE* Buffer, DWORD BufferSize, DWORD Flags);
      this.CertOpenFromBuffer = function(Buffer, Flags)
      {
         return this.Call("method", this.timeout, "CertOpenFromBuffer", Buffer, Flags);
      };

      // CertOpenFromFile(char* FileName, DWORD Flags);
      this.CertOpenFromFile = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "CertOpenFromFile", FileName, Flags);
      };

      // CertOpenFromURL(char* URL, DWORD Flags);
      this.CertOpenFromURL = function(URL, Flags)
      {
         return this.Call("method", this.timeout, "CertOpenFromURL", URL, Flags);
      };

      // CertClose(long CertID);
      this.CertClose = function(CertID)
      {
         return this.Call("method", this.timeout, "CertClose", CertID);
      };

      // CertToContainer(long CertID, char* ContainerName, DWORD ProvType, DWORD Flags);
      this.CertToContainer = function(CertID, ContainerName, ProvType, Flags)
      {
         return this.Call("method", this.timeout, "CertToContainer", CertID, ContainerName, ProvType, Flags);
      };

      // CertToSystemStore(long CertID, char* StoreName, char* ContainerName, DWORD ProvType, DWORD Flags);
      this.CertToSystemStore = function(CertID, StoreName, ContainerName, ProvType, Flags)
      {
         return this.Call("method", this.timeout, "CertToSystemStore", CertID, StoreName, ContainerName, ProvType, Flags);
      };

      // CertToStore(long CertID, long StoreID, char* ContainerName, DWORD ProvType, DWORD Flags);
      this.CertToStore = function(CertID, StoreID, ContainerName, ProvType, Flags)
      {
         return this.Call("method", this.timeout, "CertToStore", CertID, StoreID, ContainerName, ProvType, Flags);
      };

      // CertWizard(BYTE* Buffer, DWORD BufferSize, long CertType, char* ContainerName, DWORD ProvType, DWORD Flags);
      this.CertWizard = function(Buffer, CertType, ContainerName, ProvType, Flags)
      {
         return this.Call("method", this.timeout, "CertWizard", Buffer, CertType, ContainerName, ProvType, Flags);
      };

      // StoreCreate();
      this.StoreCreate = function()
      {
         return this.Call("method", this.timeout, "StoreCreate");
      };
      this.StoreCreateResponse = function(resp_data)
      {
         this.StoreID = resp_data.value;
         return resp_data.value;
      };

      // StoreClose(long StoreID);
      this.StoreClose = function()
      {
         return this.Call("method", this.timeout, "StoreClose", this.StoreID);
      };
      this.StoreCloseResponse = function(resp_data)
      {
         this.StoreID = 0;
         return resp_data.value;
      };

      // StoreAddFromBuffer(long StoreID, BYTE* Buffer, DWORD BufferSize, DWORD Flags);
      this.StoreAddFromBuffer = function(Buffer, Flags)
      {
         return this.Call("method", this.timeout, "StoreAddFromBuffer", this.StoreID, Buffer, Flags);
      };

      // StoreAddFromFile(long StoreID, char* FileName, DWORD Flags);
      this.StoreAddFromFile = function(FileName, Flags)
      {
         return this.Call("method", this.timeout, "StoreAddFromFile", this.StoreID, FileName, Flags);
      };

      // StoreAddFromURL(long StoreID, char* URL, DWORD Flags);
      this.StoreAddFromURL = function(URL, Flags)
      {
         return this.Call("method", this.timeout, "StoreAddFromURL", this.StoreID, URL, Flags);
      };

      // StoreGet(long StoreID, BYTE* Buffer, DWORD* BufferSize, DWORD Flags);
      this.StoreGet = function(Flags)
      {
         return this.Call("method", this.timeout, "StoreGet", this.StoreID, Flags);
      };

      // StoreSave(long StoreID, char* FileName, DWORD Flags);
      this.StoreSave = function(StoreID, FileName, Flags)
      {
         return this.Call("method", this.timeout, "StoreSave", this.StoreID, FileName, Flags);
      };

      // StoreToSystemStore(long StoreID, DWORD Flags);
      this.StoreToSystemStore = function(Flags)
      {
         return this.Call("method", this.timeout, "StoreToSystemStore", this.StoreID, Flags);
      };

      // StoreLinkToMessage(long StoreID, long MessageID, DWORD Flags);
      this.StoreLinkToMessage = function(MessageID,  Flags)
      {
         return this.Call("method", this.timeout, "StoreLinkToMessage", this.StoreID, MessageID, Flags);
      };

      // CheckFile(char* FilePath, char* TypeList, DWORD MaxSize)
      this.CheckFile = function(FilePath, TypeList, MaxSize)
      {
         return this.Call("method", this.timeout, "CheckFile", FilePath, TypeList, MaxSize);
      };

      // ----------------------------------------------------------------------------------------------
      // ParseFile = function(FilePath)
      this.ParseFile = function(FilePath, FileType)
      {
         return this.Call("method", this.timeout, "ParseFile", FilePath, FileType);
      };

      this.DocView = function(FileName, DocBodyBase64)
      {
         return this.Call("method", this.timeout, "DocView", FileName, DocBodyBase64);
      };

   };
    
   d1object = new d1class();
   window.d1object = d1object;

}());