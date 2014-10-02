program weather_ctest;

uses
  Forms,
  weather_test in 'weather_test.pas' {Form1};

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.
