<?php
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Imports\QuestionImport;
use App\Imports\AnswerImport;
use Maatwebsite\Excel\Facades\Excel;

class QuestionController extends Controller
{
    public function showUploadForm()
    {
        return view('questions.upload', [
            'activePage' => 'questions',
            'activeButton' => 'questions',
            'navName' => 'Questions/Answers'
        ]);
    }

    public function upload(Request $request)
    {
        $request->validate([
            'question_file' => 'required|mimes:xlsx,xls',
            'answer_file' => 'required|mimes:xlsx,xls'
        ]);

        try {
            Excel::import(new QuestionImport, $request->file('question_file'));
            Excel::import(new AnswerImport, $request->file('answer_file'));

            return redirect()->route('questions.upload')->with('success', 'Questions and Answers uploaded successfully!');
        } catch (\Exception $e) {
            return redirect()->route('questions.upload')->with('error', 'Error uploading files: ' . $e->getMessage());
        }
    }
}