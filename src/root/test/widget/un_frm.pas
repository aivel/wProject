unit un_frm;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ExtCtrls, Menus;

type
  Tfrm = class(TForm)
    img_back: TImage;
    dlg_font: TFontDialog;
    pm: TPopupMenu;
    N1: TMenuItem;
    lbl_1: TLabel;
    lbl_2: TLabel;
    lbl_3: TLabel;
    N2: TMenuItem;
    N3: TMenuItem;
    lbl_4: TLabel;
    procedure lbl_1MouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Integer);
    procedure N1Click(Sender: TObject);
    procedure N3Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frm: Tfrm;

implementation

{$R *.dfm}

procedure Tfrm.lbl_1MouseDown(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Integer);
begin
 ReleaseCapture;
 frm.Perform(WM_SYSCOMMAND, SC_MOVE + 2, 0);
end;

procedure Tfrm.N1Click(Sender: TObject);
begin
 dlg_font.Font:=lbl_1.Font;
 if dlg_font.Execute() then
  begin
   lbl_1.Font:=dlg_font.Font;
   lbl_2.Font:=dlg_font.Font;
   lbl_3.Font:=dlg_font.Font;
   lbl_4.Font:=dlg_font.Font;
   
   frm.Canvas.Font:=dlg_font.Font;
  end;
end;

procedure Tfrm.N3Click(Sender: TObject);
begin
 halt(0);
end;

end.
