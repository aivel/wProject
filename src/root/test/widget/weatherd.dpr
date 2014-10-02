library weatherd;

uses
  SysUtils,
  Classes,
  Dialogs,
  Messages,
  Windows,
  un_frm in 'un_frm.pas' {frm};

function start_app(): boolean; export; cdecl;
begin
 frm:=Tfrm.Create(nil);
 frm.Show;
 result:=true;
end;

function set_weather(f1, f2, f3, f4: pchar): boolean; export; cdecl;
begin
 frm.lbl_1.Caption:=string(f1);
 frm.lbl_2.Caption:=string(f2);
 frm.lbl_3.Caption:=string(f3);
 frm.lbl_4.Caption:=string(f4);
 result:=true;
end;

function destroy_app(): boolean; export; cdecl;
begin
 frm.Free;
 frm:=nil;
 result:=true;
end;

function mainloop(): boolean; export; cdecl;
var
 mmsg: msg;
begin
 getmessage(mmsg, 0, 0, 0);

 translatemessage(mmsg);
 dispatchmessage(mmsg);
 result:=true;
end;

exports
 start_app,
 set_weather,
 destroy_app,
 mainloop;

begin
end.
