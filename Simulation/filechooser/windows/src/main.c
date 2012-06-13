#include <stdio.h>
#include <windows.h>

int showDialog(char * path, char * filter, int open)
{
	OPENFILENAME ofn;       // common dialog box structure
	char szFile[512];       // buffer for file name
	int result;

	// Initialize OPENFILENAME
	ZeroMemory(&ofn, sizeof(ofn));
	ofn.lStructSize = sizeof(ofn);
	ofn.hwndOwner = NULL;
	ofn.lpstrFile = szFile;

	strncpy(szFile, path, sizeof(szFile));

	ofn.nMaxFile = sizeof(szFile);
	ofn.lpstrFilter = filter;
	ofn.nFilterIndex = 1;
	ofn.lpstrFileTitle = NULL;
	ofn.nMaxFileTitle = 0;
	ofn.lpstrInitialDir = NULL;

	// Display the Open dialog box. 

	if(open) {
		ofn.Flags = OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST | OFN_HIDEREADONLY;
		result = GetOpenFileName(&ofn);
	} else {
		ofn.Flags = OFN_PATHMUSTEXIST;
		result = GetSaveFileName(&ofn);
	}

	if (result != 0) {
		printf("FileSelected:%s\n", ofn.lpstrFile);
		return 0;
	} else {
		printf("result %i / %04x\n", result, CommDlgExtendedError());
	}


	return 2;
}

int showSaveDialog(char * path, char * filter)
{
	return 3;
}

int main(int argc, char ** argv)
{
	char * filter;
	char * tmp;
	int filterLen;
	int i;
	int open;

	if(argc < 4) {
		printf("wrong parameter count.\n");
		return 1;
	}

	filter = argv[2];
	filterLen = strlen(filter);
	for(i = 0; i < filterLen; i++) {
		if(filter[i] == '|') {
			filter[i] = 0;
		}
	}

	tmp = filter;
	filterLen++;
	filter = (char*)malloc(filterLen + 1);
	memcpy(filter, tmp, filterLen);
	filter[filterLen - 1] = 0;
	filter[filterLen] = 0;

	open = !strcmp(argv[1], "open");

	return showDialog(argv[3], filter, open);
}

