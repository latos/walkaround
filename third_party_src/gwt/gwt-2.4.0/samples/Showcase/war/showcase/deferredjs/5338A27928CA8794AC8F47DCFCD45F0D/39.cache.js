function YN(){}
function TN(){}
function Hyb(){}
function Lyb(){}
function s9b(){}
function z9b(a,b){Ev(a.a,b)}
function Iyb(a,b){this.b=a;this.a=b}
function Myb(a,b){this.b=a;this.a=b}
function yO(a){return S7(jO,a)}
function XN(){XN=LCc;WN=new YN}
function yyb(a,b){C0b(b,Ugd+a.Og()+OFc+a.Pg())}
function P9b(){I9b();N9b.call(this,Bl($doc,dhd),ehd)}
function gkc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function fkc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(fhd,-65535)}catch(a){return 0}}
function xyb(a,b){var c,d;c=new M5b;c.e[EQc]=4;J5b(c,a);if(b){d=new F0b(Tgd);Ie(a,new Iyb(a,d),(rr(),rr(),qr));Ie(a,new Myb(a,d),(xq(),xq(),wq));J5b(c,d)}return c}
function ikc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(fhd,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints(hhd,f)<=0){e+=2;f.moveEnd(fhd,-1);g=f.text.length}return d+e}catch(a){return 0}}
function hkc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint(ghd,c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(fhd,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(fhd,-1);i=g.text.length}return e+f}catch(a){return 0}}
var Zgd='<b>Zone de texte normale:<\/b>',ahd='<br><br><b>Zone de texte &laquo;mot de passe&raquo;:<\/b>',chd='<br><br><b>Zone de texte:<\/b>',ihd='AnyRtlDirectionEstimator',jhd='CwBasicText$2',khd='CwBasicText$3',ghd='EndToStart',lhd='PasswordTextBox',hhd='StartToEnd',Ugd='S\xE9lectionn\xE9: ',Tgd='S\xE9lectionn\xE9: 0, 0',fhd='character',$gd='cwBasicText-password',_gd='cwBasicText-password-disabled',bhd='cwBasicText-textarea',Vgd='cwBasicText-textbox',Wgd='cwBasicText-textbox-disabled',ehd='gwt-PasswordTextBox',Xgd='lecture seulement',dhd='password';_=YN.prototype=TN.prototype=new UN;_.le=function ZN(a){return yO((sO(),a))?(Az(),zz):(Az(),yz)};_.gC=function $N(){return lT};var WN;_=Cyb.prototype;_.bc=function Gyb(){var a,b,c,d,e,f;X9(this.a,(f=new xic,f.e[EQc]=5,d=new M9b,Vhc(d.Q,fDc,Vgd),z9b(d,(XN(),XN(),WN)),b=new M9b,Vhc(b.Q,fDc,Wgd),b.Q[S0c]=Xgd,Dv(b.a),b.Q[Ygd]=true,uic(f,new M0b(Zgd)),uic(f,xyb(d,true)),uic(f,xyb(b,false)),c=new P9b,Vhc(c.Q,fDc,$gd),a=new P9b,Vhc(a.Q,fDc,_gd),a.Q[S0c]=Xgd,Dv(a.a),a.Q[Ygd]=true,uic(f,new M0b(ahd)),uic(f,xyb(c,true)),uic(f,xyb(a,false)),e=new Xfc,Vhc(e.Q,fDc,bhd),e.Q.rows=5,uic(f,new M0b(chd)),uic(f,xyb(e,true)),f))};_=Iyb.prototype=Hyb.prototype=new Y;_.gC=function Jyb(){return gY};_.sc=function Kyb(a){yyb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=Myb.prototype=Lyb.prototype=new Y;_.gC=function Nyb(){return hY};_.qc=function Oyb(a){yyb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=v9b.prototype;_.Og=function D9b(){return fkc(this.Q)};_.Pg=function E9b(){return gkc(this.Q)};_=P9b.prototype=s9b.prototype=new t9b;_.gC=function Q9b(){return P0};_.cM={40:1,46:1,85:1,92:1,96:1,111:1,113:1};_=Wfc.prototype;_.Og=function Zfc(){return hkc(this.Q)};_.Pg=function $fc(){return ikc(this.Q)};var lT=Kqc(bVc,ihd),gY=Kqc(KXc,jhd),hY=Kqc(KXc,khd),P0=Kqc(CSc,lhd);bDc(sj)(39);