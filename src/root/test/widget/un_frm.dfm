object frm: Tfrm
  Left = 345
  Top = 218
  BorderStyle = bsNone
  Caption = 'weatherd'
  ClientHeight = 173
  ClientWidth = 1105
  Color = 16744576
  TransparentColor = True
  TransparentColorValue = 16744576
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  FormStyle = fsStayOnTop
  OldCreateOrder = False
  PopupMenu = pm
  Position = poDesktopCenter
  OnMouseDown = lbl_1MouseDown
  PixelsPerInch = 96
  TextHeight = 13
  object img_back: TImage
    Left = 0
    Top = 0
    Width = 1105
    Height = 173
    Align = alClient
    PopupMenu = pm
    Stretch = True
    OnMouseDown = lbl_1MouseDown
  end
  object lbl_1: TLabel
    Left = 8
    Top = 8
    Width = 58
    Height = 35
    Caption = 'lbl_1'
    Font.Charset = RUSSIAN_CHARSET
    Font.Color = clSilver
    Font.Height = -24
    Font.Name = 'Comic Sans MS'
    Font.Style = [fsBold]
    ParentFont = False
    PopupMenu = pm
    OnMouseDown = lbl_1MouseDown
  end
  object lbl_2: TLabel
    Left = 8
    Top = 48
    Width = 58
    Height = 35
    Caption = 'lbl_2'
    Font.Charset = RUSSIAN_CHARSET
    Font.Color = clSilver
    Font.Height = -24
    Font.Name = 'Comic Sans MS'
    Font.Style = [fsBold]
    ParentFont = False
    PopupMenu = pm
    OnMouseDown = lbl_1MouseDown
  end
  object lbl_3: TLabel
    Left = 8
    Top = 88
    Width = 58
    Height = 35
    Caption = 'lbl_3'
    Font.Charset = ANSI_CHARSET
    Font.Color = clSilver
    Font.Height = -24
    Font.Name = 'Comic Sans MS'
    Font.Style = [fsBold]
    ParentFont = False
    PopupMenu = pm
    OnMouseDown = lbl_1MouseDown
  end
  object lbl_4: TLabel
    Left = 8
    Top = 128
    Width = 58
    Height = 35
    Caption = 'lbl_4'
    Font.Charset = ANSI_CHARSET
    Font.Color = clSilver
    Font.Height = -24
    Font.Name = 'Comic Sans MS'
    Font.Style = [fsBold]
    ParentFont = False
    PopupMenu = pm
    OnMouseDown = lbl_1MouseDown
  end
  object dlg_font: TFontDialog
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -11
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    Left = 256
    Top = 64
  end
  object pm: TPopupMenu
    Left = 320
    Top = 64
    object N1: TMenuItem
      Caption = 'F&ont'
      OnClick = N1Click
    end
    object N2: TMenuItem
      Caption = '-'
    end
    object N3: TMenuItem
      Caption = '&Exit'
      OnClick = N3Click
    end
  end
end
