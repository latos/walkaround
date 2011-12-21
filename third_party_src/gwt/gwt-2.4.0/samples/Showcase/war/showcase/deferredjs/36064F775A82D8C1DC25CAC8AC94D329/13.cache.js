function cA(){}
function eA(){eA=lRc;dA=new oOc}
function Oic(a,b,c,d){var e;a.b.Ig(b,c);e=Qic(a.b.j,b,c);Pe(e,d,true)}
function fA(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ce(c)}}
function hA(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Gd(a);return String(c)}
function kA(){eA();var a;a=W3(dA.Zd(BYc),51);if(!a){a=new iA;dA._d(BYc,a)}return a}
function gA(c,b){try{typeof $wnd[b]!='object'&&mA(b);c.b=$wnd[b]}catch(a){mA(b)}}
function mA(a){throw new wPc(iTc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function iA(){this.c='Dictionary userInfo';gA(this,BYc);if(!this.b){throw new wPc("Cannot find JavaScript object with the name 'userInfo'")}}
function SFb(){var a,b,c,d,e,f,g,i,j,k,n;f=new hxc;g=new Mfc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=CSc;g.R.style['textAlign']=XSc;exc(f,new Mfc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));exc(f,g);j=new Fic;b=j.k;i=kA();e=(n=new xOc,fA(i,n),n);a=0;for(d=dLc($G(e.b));d.b.ie();){c=W3(kLc(d),1);k=hA(i,c);vic(j,0,a,c);Oic(b,0,a,'cw-DictionaryExample-header');vic(j,1,a,k);Oic(b,1,a,'cw-DictionaryExample-data');++a}exc(f,new Mfc('<br><br>'));exc(f,j);return f}
var BYc='userInfo';_=iA.prototype=cA.prototype=new Y;_.gC=function jA(){return o7};_.Gd=function lA(a){var b;b="Cannot find '"+a+"' in "+this;throw new wPc(b)};_.tS=function nA(){return this.c};_.cM={51:1};_.b=null;_.c=null;var dA;_=WFb.prototype;_.fc=function $Fb(){oqb(this.b,SFb())};var o7=kFc(DWc,'Dictionary');DRc(Hj)(13);