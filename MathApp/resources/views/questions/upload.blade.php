@extends('layouts.app', ['activePage' => 'questions', 'activeButton' => 'questions', 'navName' => 'Questions/Answers'])

@section('content')
    <div class="content">
        <div class="container-fluid">
            <div class="card">
                <div class="card-header">
                    <h4 class="card-title">Upload Questions/Answers</h4>
                </div>
                <div class="card-body">
                    @if (session('success'))
                        <div class="alert alert-success">
                            {{ session('success') }}
                        </div>
                    @endif
                    @if (session('error'))
                        <div class="alert alert-danger">
                            {{ session('error') }}
                        </div>
                    @endif
                    <form action="{{ route('questions.upload') }}" method="POST" enctype="multipart/form-data">
                        @csrf
                        <div class="form-group">
                            <label for="question_file">Select Questions Excel File</label>
                            <input type="file" name="question_file" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label for="answer_file">Select Answers Excel File</label>
                            <input type="file" name="answer_file" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Upload</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection