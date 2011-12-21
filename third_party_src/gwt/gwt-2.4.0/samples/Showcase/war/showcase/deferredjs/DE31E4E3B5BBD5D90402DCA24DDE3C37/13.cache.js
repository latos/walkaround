function Bz(){}
function Dz(){Dz=sQc;Cz=new vNc}
function fic(a,b,c,d){var e;a.b.Eg(b,c);e=hic(a.b.j,b,c);Ce(e,d,true)}
function Ez(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function Gz(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function Jz(){Dz();var a;a=t3(Cz.Vd(DXc),51);if(!a){a=new Hz;Cz.Xd(DXc,a)}return a}
function Fz(c,b){try{typeof $wnd[b]!='object'&&Lz(b);c.b=$wnd[b]}catch(a){Lz(b)}}
function Lz(a){throw new DOc(qSc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Hz(){this.c='Dictionary userInfo';Fz(this,DXc);if(!this.b){throw new DOc("Cannot find JavaScript object with the name 'userInfo'")}}
function mFb(){var a,b,c,d,e,f,g,i,j,k,n;f=new Awc;g=new Zec('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=KRc;g.R.style['textAlign']=dSc;xwc(f,new Zec('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));xwc(f,g);j=new Yhc;b=j.k;i=Jz();e=(n=new ENc,Ez(i,n),n);a=0;for(d=kKc(xG(e.b));d.b.ee();){c=t3(rKc(d),1);k=Gz(i,c);Ohc(j,0,a,c);fic(b,0,a,'cw-DictionaryExample-header');Ohc(j,1,a,k);fic(b,1,a,'cw-DictionaryExample-data');++a}xwc(f,new Zec('<br><br>'));xwc(f,j);return f}
var DXc='userInfo';_=Hz.prototype=Bz.prototype=new Y;_.gC=function Iz(){return L6};_.Cd=function Kz(a){var b;b="Cannot find '"+a+"' in "+this;throw new DOc(b)};_.tS=function Mz(){return this.c};_.cM={51:1};_.b=null;_.c=null;var Cz;_=qFb.prototype;_.bc=function uFb(){Kpb(this.b,mFb())};var L6=rEc(FVc,'Dictionary');KQc(tj)(13);