

function f_check(timeout)
{
    console.log("Checking extension and plugin...");
    if (window.d1object) {
        window.d1object.Check(3, timeout,
            function (ret) {
                alert('Ok - ' + ret);
                console.log(ret);
            },
            function (ret) {
                alert('Bad - ' + ret);
                console.log(ret);
            }
        )
    }
    else
       alert ('Библиотека не загружена');
}

function f_get_methods(id_txt_methods)
   {
   console.log("Executing GetMethods...");
   window.d1object.GetMethods().then(
      function(response)
      {
         console.log("GetMethod response: " + response);
         document.getElementById(id_txt_methods).value = JSON.stringify(response, undefined, 2);
         return 1;
      },
      function(error)
      {
         console.log(error);
         return;
      }
   );
}

function f_checkAndSign(timeout, cert_id, id_txt_data, id_txt_sign)
{
    console.log("Checking extension and plugin...");
    if (window.d1object) {
        window.d1object.Check(3, timeout,
            function (ret) {
                console.log(ret);
                return f_sign(cert_id, id_txt_data, id_txt_sign);
            },
            function (ret) {
                alert('Не установлено расширение');
                console.log(ret);
            }
        );
    }
    else
       alert ('Библиотека не загружена');
}

function f_sign(cert_id, data, callback)
{
   var MsgID;

   console.log("Executing CreateSession...");
   window.d1object.CreateSession(cert_id, 4096).then(
      function(ret)
      {  
         console.log("SessionID: ", ret);
         console.log("Executing CreateMessageFromBuffer...");
         return window.d1object.CreateMessageFromBuffer(data, 0);
      },
      function(ret)
      {  
         console.log("CreateSession error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("Document ID: ", ret);
         MsgID = ret;
         console.log("Executing SignMessage...");
         return window.d1object.SignMessage(ret, 4096);
      },
      function(ret)
      {  
         console.log("CreateMessageFromBuffer error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("SignMessage response: ", ret);
         console.log("Executing GetMessageBody...");
         return window.d1object.GetMessageBody(MsgID, 1);
      },
      function(ret)
      {  
         console.log("SignMessage error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         // console.log("GetMessageBody response: ", ret);
          callback(ret);
         // document.getElementById(id_txt_sign).value = ret;
         // signCount = signCount - 1;
         /*if (signCount === 0) {
             if  (packetSize)
                 podpPAKET_EDOC(document.getElementById('paketsSize').value, document.getElementById('docType').value);
            /!* else
                 document.form.submit();*!/
         }*/
         console.log("Executing ReleaseMessage...");
         return window.d1object.ReleaseMessage(MsgID);
      },
      function(ret)
      {
         console.log("GetMessageBody error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("ReleaseMessage response: ", ret);
         console.log("Executing CloseSession...");
         return window.d1object.CloseSession();
      },
      function(ret)
      {
         console.log("ReleaseMessage error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("CloseSession response: ", ret);
      },
      function(ret)
      {
         console.log("CloseSession error: ", ret);
      }
   );
}

function f_verify(cert_id, id_txt_sign, id_txt_result, id_txt_data, id_txt_sign_info)
{
   var MsgID;

   console.log("Executing CreateSession...");
   window.d1object.CreateSession(cert_id, 4096).then(
      function(ret)
      {  
         console.log("SessionID: ", ret);
         console.log("Executing OpenMessageFromBuffer...");
         var Msg = document.getElementById(id_txt_sign).value;
         return window.d1object.OpenMessageFromBuffer(Msg, 1)
      },
      function(ret)
      {  
         console.log("CreateSession error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("Document ID: ", ret);
         MsgID = ret;
         console.log("Executing VerifyMessage...");
         return window.d1object.VerifyMessage(MsgID, 0, 4096);
      },
      function(ret)
      {  
         console.log("OpenMessageFromBuffer error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("VerifyMessage response: ", ret, ", sign is TRUE!");
         document.getElementById(id_txt_result).innerHTML = "<b>Sign is TRUE</b>";
         return window.d1object.GetMessageContent(MsgID, 0);
      },
      function(ret)
      {  
         console.log("VerifyMessage error: ", ret, ", sign is FALSE!");
         document.getElementById(id_txt_result).innerHTML = "<b>Sign is FALSE</b>";
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("GetMessageContent response: ", ret);
         document.getElementById(id_txt_data).value = ret; 

         console.log("Executing GetMessageInfos...");
         return window.d1object.GetMessageInfos(MsgID, 0, 32);
      },
      function(ret)
      {
         console.log("GetMessageContent error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("GetMessageInfos response: ", ret);
         document.getElementById(id_txt_sign_info).value = JSON.stringify(ret, undefined, 2);
         console.log("Executing ReleaseMessage...");
         return window.d1object.ReleaseMessage(MsgID);
      },
      function(ret)
      {
         console.log("GetMessageInfos error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("ReleaseMessage response: ", ret);
         console.log("Executing CloseSession...");
         return window.d1object.CloseSession();
      },
      function(ret)
      {
         console.log("ReleaseMessage error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("CloseSession response: ", ret);
      },
      function(ret)
      {
         console.log("CloseSession error: ", ret);
      }
   );
}

function f_save2file()
{
   var MsgID;

   console.log("Executing OpenMessageFromBuffer...");
   var Msg = document.getElementById("txt_sign").value;
   window.d1object.OpenMessageFromBuffer(Msg, 1).then(
      function(ret)
      {
         MsgID = ret;
         console.log("MsgID: ", ret);
         console.log("Executing SaveMessageBody...");
         return window.d1object.SaveMessageBody(MsgID, "d:\\d1nmh_test.der", 0);
      },
      function(ret)
      {  
         console.log("OpenMessageFromBuffer error: ", ret);
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("SaveMessageBody response: ", ret);
         document.getElementById("div_save2file").innerHTML = "<b>File saved</b>";
         return window.d1object.ReleaseMessage(MsgID);
      },
      function(ret)
      {
         console.log("SaveMessageBody error: ", ret);
         document.getElementById("div_save2file").innerHTML = "<b>File NOT saved</b>";
         throw {ret};
      }
   ).then(
      function(ret)
      {
         console.log("ReleaseMessage response: ", ret);
      },
      function(ret)
      {
         console.log("ReleaseMessage error: ", ret);
      }
   );
}

function f_check_file(file_name, type_list, max_size)
   {
   console.log("Executing CheckFile...");
   window.d1object.CheckFile(file_name, type_list, max_size).then(
      function(response)
      {
         console.log("CheckFile response: " + response);
         return response;
      },
      function(error)
      {
         console.log(error);
         return;
      }
   );
}

function openPdfOutLine(fiename, body) {
    console.log("Executing CheckFile...");
    window.d1object.DocView(fiename, body).then(
        function (response) {
            console.log("DocView response: " + response);
            document.getElementById("div_pdf_progress").innerHTML = 'Документ загружен и открыт во внешнем приложении';
            setTimeout('window.close()', 5000);
            return 1;
        },
        function (error) {
            console.log(error);
            document.getElementById("div_pdf_progress").innerHTML = 'Документ загружен, но не открыт - отсутствует внешнее приложение';
            return 0;
        }
    );
}

