unit weather_test;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls;

type
  TForm1 = class(TForm)
    btn: TButton;
    procedure btnClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;

implementation

function start_app(): boolean; cdecl; external 'weatherd.dll';
function set_weather(ctemp, cpressure, cwind: pchar): boolean; cdecl; external 'weatherd.dll';
function destroy_app(): boolean; cdecl; external 'weatherd.dll';

{$R *.dfm}

procedure TForm1.btnClick(Sender: TObject);
begin
 start_app();
 //set_weather('1', '2', '3');
 //destroy_app();
end;

end.
