function $eb(){}
function Veb(){}
function M_b(){}
function Q_b(){}
function zBc(){}
function GBc(a,b){vv(a.a,b)}
function N_b(a,b){this.b=a;this.a=b}
function R_b(a,b){this.b=a;this.a=b}
function Afb(a){return pBb(lfb,a)}
function Zeb(){Zeb=x2c;Yeb=new $eb}
function D_b(a,b){Psc(b,'Selected: '+a.Gg()+Q3c+a.Hg())}
function WBc(){PBc();UBc.call(this,Bl($doc,'password'),'gwt-PasswordTextBox')}
function ULc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function TLc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(Ccd,-65535)}catch(a){return 0}}
function C_b(a,b){var c,d;c=new Wxc;c.e[$6c]=4;Txc(c,a);if(b){d=new Ssc('Selected: 0, 0');Ie(a,new N_b(a,d),(ir(),ir(),hr));Ie(a,new R_b(a,d),(oq(),oq(),nq));Txc(c,d)}return c}
function WLc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(Ccd,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints('StartToEnd',f)<=0){e+=2;f.moveEnd(Ccd,-1);g=f.text.length}return d+e}catch(a){return 0}}
function VLc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint('EndToStart',c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(Ccd,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(Ccd,-1);i=g.text.length}return e+f}catch(a){return 0}}
var Ccd='character',Acd='read only';_=$eb.prototype=Veb.prototype=new Web;_.je=function _eb(a){return Afb((ufb(),a))?(HA(),GA):(HA(),FA)};_.gC=function afb(){return tlb};var Yeb;_=H_b.prototype;_.ac=function L_b(){var a,b,c,d,e,f;uDb(this.a,(f=new wKc,f.e[$6c]=5,d=new TBc,UJc(d.Q,R2c,'cwBasicText-textbox'),GBc(d,(Zeb(),Zeb(),Yeb)),b=new TBc,UJc(b.Q,R2c,'cwBasicText-textbox-disabled'),b.Q[I8c]=Acd,uv(b.a),b.Q[Bcd]=true,tKc(f,new Zsc('<b>Normal text box:<\/b>')),tKc(f,C_b(d,true)),tKc(f,C_b(b,false)),c=new WBc,UJc(c.Q,R2c,'cwBasicText-password'),a=new WBc,UJc(a.Q,R2c,'cwBasicText-password-disabled'),a.Q[I8c]=Acd,uv(a.a),a.Q[Bcd]=true,tKc(f,new Zsc('<br><br><b>Password text box:<\/b>')),tKc(f,C_b(c,true)),tKc(f,C_b(a,false)),e=new YHc,UJc(e.Q,R2c,'cwBasicText-textarea'),e.Q.rows=5,tKc(f,new Zsc('<br><br><b>Text area:<\/b>')),tKc(f,C_b(e,true)),f))};_=N_b.prototype=M_b.prototype=new Y;_.gC=function O_b(){return lqb};_.qc=function P_b(a){D_b(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=R_b.prototype=Q_b.prototype=new Y;_.gC=function S_b(){return mqb};_.oc=function T_b(a){D_b(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=CBc.prototype;_.Gg=function KBc(){return TLc(this.Q)};_.Hg=function LBc(){return ULc(this.Q)};_=WBc.prototype=zBc.prototype=new ABc;_.gC=function XBc(){return Tub};_.cM={40:1,46:1,84:1,91:1,95:1,110:1,112:1};_=XHc.prototype;_.Gg=function $Hc(){return VLc(this.Q)};_.Hg=function _Hc(){return WLc(this.Q)};var tlb=wSc(Z7c,'AnyRtlDirectionEstimator'),lqb=wSc(k8c,'CwBasicText$2'),mqb=wSc(k8c,'CwBasicText$3'),Tub=wSc(J7c,'PasswordTextBox');P2c(sj)(39);