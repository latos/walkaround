function aTb(){}
function eTb(){}
function fTb(a){this.a=a}
function bTb(a,b){this.a=a;this.b=b}
function YSb(a){var b,c,d;b=wTc(ul(a.a.Q,o8c));c=wTc(ul(a.b.Q,o8c));d=wTc(ul(a.c.Q,o8c));xsc(a.d,"User '"+b+"' has security clearance '"+c+"' and cannot access '"+d+$3c)}
function XSb(a){var b,c,d,e,f,g;d=new Avc;b=tgb(d.j,90);d.o[H6c]=5;g=PCb(Kob);e=new onc(g);Je(e,new bTb(a,g),(dq(),dq(),cq));f=new Exc;f.e[H6c]=3;Bxc(f,new Hsc(Y8c));Bxc(f,e);tvc(d,0,0,f);Kvc(b,0)[Z8c]=2;qvc(d,1,0,$8c);qvc(d,1,1,"User '{0}' has security clearance '{1}' and cannot access '{2}'");a.a=new BBc;pBc(a.a,'amelie');qvc(d,2,0,_8c);tvc(d,2,1,a.a);a.b=new BBc;pBc(a.b,'guest');qvc(d,3,0,a9c);tvc(d,3,1,a.b);a.c=new BBc;pBc(a.c,'/secure/blueprints.xml');qvc(d,4,0,b9c);tvc(d,4,1,a.c);a.d=new Fsc;qvc(d,5,0,F9c);tvc(d,5,1,a.d);Pvc(b,5,0,(Vwc(),Uwc));c=new fTb(a);Je(a.a,c,(Zq(),Zq(),Yq));Je(a.b,c,Yq);Je(a.c,c,Yq);YSb(a);return d}
_=bTb.prototype=aTb.prototype=new Y;_.gC=function cTb(){return xob};_.oc=function dTb(a){HCb(this.a,this.b+f9c)};_.cM={22:1,44:1};_.a=null;_.b=null;_=fTb.prototype=eTb.prototype=new Y;_.gC=function gTb(){return yob};_.qc=function hTb(a){YSb(this.a)};_.cM={27:1,44:1};_.a=null;_=iTb.prototype;_.ac=function mTb(){jDb(this.b,XSb(this.a))};var Kob=eSc(M7c,'ErrorMessages'),xob=cSc(M7c,'CwMessagesExample$1'),yob=cSc(M7c,'CwMessagesExample$2');v2c(tj)(14);